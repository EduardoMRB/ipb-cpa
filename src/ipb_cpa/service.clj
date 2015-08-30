(ns ipb-cpa.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [ipb-cpa.view.home :as home-view]
            [ipb-cpa.view.contact :as contact-view]
            [ipb-cpa.view.admin-view :as admin-view]
            [ipb-cpa.view.about :as about-view]
            [ipb-cpa.db :as database]))

(defn home-page [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (home-view/index db))))

(defn contact-page [_]
  (ring-resp/response (contact-view/contact)))

(defn about-page [_]
  (ring-resp/response (about-view/about)))

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

(defroutes routes
  [[["/" ^:interceptors [(body-params/body-params) bootstrap/html-body]
     {:get [:site#index home-page]}
     ["/sobre" {:get [:site#about about-page]}]
     ["/contato" {:get [:site#contact contact-page]}]
     ["/admin" {:get [:admin#dashboard dashboard-page]}
      ["/login" {:get [:admin#login admin-login-page]}]
      ["/schedule" {:get [:admin.schedule#index admin-schedule-page]}]
      ["/video" {:get [:admin.video#index admin-video-page]}]]]
    ["/api" ^:interceptors [(body-params/body-params) bootstrap/json-body]
     ["/schedule" {:get [:api.schedule#index get-json-schedules]
                   :post [:api.schedule#create add-schedule]}
      ["/:id" {:delete [:api.schedule#delete delete-schedule]
               :put [:api.schedule#update update-schedule]}]]]]])

;; Consumed by ipb-cpa.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::bootstrap/type :jetty
              ;;::bootstrap/host "localhost"
              ::bootstrap/port 8080})

