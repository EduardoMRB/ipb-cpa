(ns ipb-cpa.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.interceptor.helpers :as interceptor]
            [io.pedestal.http :as bootstrap]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [ipb-cpa.service :as service]
            [ipb-cpa.system :as system]
            [ipb-cpa.server :as server]))

;; Test system.
(def test-system
  (interceptor/on-request
   (fn [request]
     (let [system-map (system/system (env :test-db-connection-uri))]
       (assoc request :system (component/start system-map))))))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet
                           (server/attach-interceptor service/service
                                                      test-system))))

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
