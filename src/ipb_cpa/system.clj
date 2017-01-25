(ns ipb-cpa.system
  (:require [com.stuartsierra.component :as component]
            [io.pedestal
             [http :as http]
             [log :as log]]
            [io.pedestal.http.route :as route]
            [ipb-cpa
             [mail :as mail]
             [service :as service]]))

;; Components
(defrecord Database [connection-uri]
  component/Lifecycle
  (start [component]
    (assoc component :db {:connection-uri connection-uri}))
  (stop [component]
    (dissoc component :db)))

(defn make-database [connection-uri]
  (->Database connection-uri))

(defrecord Mailer [host port user pass]
  mail/IMailer
  (send-mail [this to from message]
    (mail/postal-send-mail this to from message)))

(defn make-mailer [{:keys [host port user pass]}]
  (->Mailer host port user pass))

(defn system-interceptor [k comp]
  {:name k
   :enter (fn [ctx]
            (assoc-in ctx [:request :system k] comp))})

(defn attach-component-interceptor [service-map comp-interceptor]
  (update service-map
          ::http/interceptors
          #(conj % comp-interceptor)))

(def err-interceptor
  {:name ::error-interceptor
   :error (fn [context error]
            (log/error :exception error)
            (assoc context :request
                   {:status 500
                    :body (.getMessage error)
                    :headers {"Content-Type" "text/httml;charset=UTF-8;"}}))})

(defrecord WebServer [env port runnable-service database mailer]
  component/Lifecycle
  (start [this]
    (if runnable-service
      this
      (let [service-map (-> service/service
                            (assoc ::http/port port)
                            http/default-interceptors
                            (attach-component-interceptor (system-interceptor :database database))
                            (attach-component-interceptor (system-interceptor :mailer mailer)))]
        (log/info :message (str "Starting WebServer on port: " port))
        (if (= env :dev)
          (assoc this :runnable-service (-> service-map
                                            (merge {:env :dev
                                                    ::http/join? false
                                                    ::http/routes #(route/expand-routes (deref #'service/routes))
                                                    ::http/allowed-origins {:creds true
                                                                            :allowed-origins (constantly true)}})
                                            http/dev-interceptors
                                            http/create-server
                                            http/start))

          (assoc this :runnable-service (-> service-map
                                            http/create-server
                                            http/start))))))
  (stop [this]
    (if runnable-service
      (do (http/stop runnable-service)
          (assoc this :runnable-service nil))
      this)))

(defn make-web-server [options]
  (map->WebServer options))

;; System
(defn system [{:keys [web-server connection-uri mailer-params]}]
  (component/system-map
   :database (make-database connection-uri)
   :mailer (make-mailer mailer-params)
   :web-server (component/using
                (make-web-server web-server)
                [:database :mailer])))
