(ns ipb-cpa.service
  (:require [clojure.java.io :as io]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http
             [body-params :as body-params]
             [ring-middlewares :as ring-middlewares]
             [route :as route]]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [io.pedestal.interceptor.helpers :as interceptor]
            [ipb-cpa
             [db :as database]
             [mail :as mail]]
            [ipb-cpa.view
             [about :as about-view]
             [admin-view :as admin-view]
             [contact :as contact-view]
             [home :as home-view]
             [institutional :as institutional]]
            [ring.util.response :as ring-resp]
            [io.pedestal.log :as log]))

(declare url-for)

(defn home-page [request]
  (log/info :request request :message "Request is getting here")
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (home-view/index url-for db))))

(defn contact-page [request]
  (ring-resp/response (contact-view/contact url-for (:flash request))))

(defn send-message [request]
  (let [params       (:params request)
        mailer       (get-in request [:system :mailer])
        mail-message {:subject (str "Contato do site IPB CPA IV - " (params "name"))
                      :body (params "message")}]
    (when (mail/send-mail mailer "pastor@gmail.com" (params "email") mail-message)
      (-> (ring-resp/redirect "/contato")
          (assoc :flash "Mensagem enviada com sucesso!")))))

(defn about-page [_]
  (ring-resp/response (about-view/about url-for)))

(defn faith-symbols-page [_]
  (ring-resp/response (institutional/faith-symbols-view)))

(defn history-page [_]
  (ring-resp/response (institutional/history-view)))

(defn ministry-page [_]
  (ring-resp/response (institutional/ministry-view)))

(defn deacon-board-page [_]
  (ring-resp/response (institutional/deacon-board-view)))

(defn council-page [_]
  (ring-resp/response (institutional/council-view)))

(defn dashboard-page [_]
  (ring-resp/response (admin-view/dashboard)))

(defn admin-login-page [_]
  (ring-resp/response (admin-view/login-page)))

(defn admin-schedule-page [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (admin-view/schedule-index db))))

(defn admin-video-page [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (admin-view/video-index db))))

(defn get-json-schedules [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (database/get-schedules db))))

(defn add-schedule [request]
  (let [db (get-in request [:system :database :db])
        schedule (get-in request [:transit-params :schedule])
        schedule-id (database/add-schedule<! db schedule)]
    (ring-resp/response {:schedule-id schedule-id})))

(defn delete-schedule [request]
  (let [db (get-in request [:system :database :db])
        schedule-id (get-in request [:path-params :id])]
    (database/remove-schedule! db (Integer/parseInt schedule-id))
    (ring-resp/response {:ok true})))

(defn update-schedule [request]
  (let [db (get-in request [:system :database :db])
        schedule-id (get-in request [:path-params :id])
        schedule (get-in request [:transit-params :schedule])]
    (database/modify-schedule! db (Integer/parseInt schedule-id) schedule)
    (ring-resp/response {:ok true})))

(def cors
  (interceptor/on-response
   (fn [response]
     (assoc-in response [:headers "Access-Control-Allow-Origin"] "*"))))

(def routes
  `{"/" {:interceptors [(body-params/body-params) bootstrap/html-body cors
                        (ring-middlewares/session) (ring-middlewares/flash)]
         :route-name   :site#index
         :get          [:site#index home-page]
         "/sobre"      {:get              [:site#about about-page]
                        "/historia"       {:get [:site.about#history history-page]}
                        "/ministro"       {:get [:site.about#ministry ministry-page]}
                        "/junta-diaconal" {:get [:site.about#deacon-board deacon-board-page]}
                        "/conselho"       {:get [:site.about#council council-page]}
                        "/simbolos-de-fe" {:get [:site.about#faith-symbols faith-symbols-page]}}
         "/contato"    {:get  [:site#contact contact-page]
                        :post [:site#send-message send-message]}
         "/admin"      {:get        [:admin#dashboard dashboard-page]
                        "/login"    {:get [:admin#login admin-login-page]}
                        "/schedule" {:get [:admin.schedule#index admin-schedule-page]}
                        "/video"    {:get [:admin.video#index admin-video-page]}}
         "/api"        {:interceptors [(body-params/body-params) bootstrap/json-body]
                        "/schedule"   {:get   [:api.schedule#index get-json-schedules]
                                       :post  [:api.schedule#create add-schedule]
                                       "/:id" {:delete [:api.schedule#delete delete-schedule]
                                               :put    [:api.schedule#update update-schedule]}}}}})

(def url-for (route/url-for-routes (route/expand-routes routes)))

(def service {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/join? false
              ::bootstrap/port 8080
              ::bootstrap/container-options {:h2c? true
                                             :h2? false
                                             :ssl? false}})

