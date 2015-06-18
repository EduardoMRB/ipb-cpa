(ns ipb-cpa.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [io.pedestal.interceptor.helpers :as interceptor]
            [ipb-cpa.service :as service]
            [ns-tracker.core :refer [ns-tracker]]
            [environ.core :refer [env]]
            [ipb-cpa.system :as system]
            [com.stuartsierra.component :as component]))

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
(defonce runnable-service (server/create-server service/service))

(def modified-namespaces
  (ns-tracker "src"))

(def system-interceptor
  (interceptor/on-request
   (fn [request]
     (let [system-map (system/system (env :db-connection-uri))]
       (assoc request :system (component/start system-map))))))

(defn attach-interceptor [service-map interceptor]
  (update-in service-map [::server/interceptors] #(conj % interceptor)))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ;; ::server/routes #(deref #'service/routes)
              ::server/routes (fn []
                                (doseq [ns-sym (modified-namespaces)]
                                  (require ns-sym :reload))
                                @#'service/routes)
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      (attach-interceptor system-interceptor)
      server/create-server
      server/start))

(defn stop []
  (server/stop runnable-service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (-> service/service
      (attach-interceptor system-interceptor)
      server/create-server
      server/start))
