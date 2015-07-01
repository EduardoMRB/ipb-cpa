(ns ipb-cpa.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [ipb-cpa.site.view :as view]
            [ipb-cpa.view.admin-view :as admin-view]
            [ipb-cpa.db :as database]))

(defn home-page [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (view/index db))))

(defn contact-page [_]
  (ring-resp/response (view/contact)))

(defn admin-login-page [_]
  (ring-resp/response "Login page!"))

(defn admin-schedule-page [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (admin-view/schedule-index db))))

(defn get-json-schedules [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (admin-view/json-schedules db))))

(defn add-schedule [request]
  (let [db (get-in request [:system :database :db])
        schedule (get-in request [:transit-params :schedule])
        schedule-id (database/add-schedule<! db schedule)]
    (ring-resp/response {:schedule-id schedule-id})))

(defroutes routes
  [[["/" {:get [:site#index home-page]}
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/contato" {:get [:site#contact contact-page]}]
     ["/admin" {:get [:admin#login admin-login-page]}
      ["/schedule" {:get [:admin.schedule#index admin-schedule-page]}]]]
    ["/api" ^:interceptors [(body-params/body-params) bootstrap/json-body]
     ["/schedule" {:get [:api.schedule#index get-json-schedules]
                   :post [:api.schedule#create add-schedule]}]]]])

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

