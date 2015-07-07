(ns ipb-cpa.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.interceptor.helpers :as interceptor]
            [io.pedestal.http :as bootstrap]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [ipb-cpa.service :as service]
            [ipb-cpa.server :as server]
            [ipb-cpa.system :as system]))

;; Test system.
(def test-system-interceptor
  (interceptor/on-request
   (fn [request]
     (let [smap (system/system (env :test-db-connection-uri))]
       (assoc request :system (component/start smap))))))

(def service
  (-> service/service
      bootstrap/default-interceptors
      (server/attach-interceptor test-system-interceptor)
      bootstrap/service-fn
      ::bootstrap/service-fn))

;; Tests.
(facts home-page-test
  (get-in (response-for service :get "/")
          [:headers "Content-Type"]) => (contains "text/html"))

(facts "contact page"
  (let [cont-resp (response-for service :get "/contato")]
    (fact "contact page returns 200 status code"
      (:status cont-resp) => 200)
    (fact "contact page content is html"
      (get-in cont-resp [:headers "Content-Type"]) => "text/html;charset=UTF-8")))

(facts "admin"
  (let [resp (response-for service :get "/admin")]
    (fact "admin login page is html"
      (get-in resp
              [:headers "Content-Type"]) => "text/html;charset=UTF-8")
    (fact "admin login page contains the word 'Login'"
      (:body resp) => (contains "Login"))))

(facts schedule-json-api
  (let [resp (response-for service :get "/api/schedule")]
    (fact "handler returns json"
      (get-in resp [:headers "Content-Type"]) => "application/json;charset=UTF-8")
    (fact "returns schedules in body"
      (:body resp) => "[]")))

(facts admin-videos-page
  (fact "content-type is html"
    (get-in (response-for service :get "/admin/video")
            [:headers "Content-Type"]) => "text/html;charset=UTF-8"))
