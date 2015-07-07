(ns ipb-cpa.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.interceptor.helpers :as interceptor]
            [io.pedestal.http :as bootstrap]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [ipb-cpa.service :as service]
            [ipb-cpa.server :as server]))

;; Test system.
(defrecord TestDatabase [subprotocol subname]
  component/Lifecycle
  (start [component]
    (assoc component :db {:subprotocol subprotocol
                          :subname subname}))
  (stop [component]
    (dissoc component :db)))

(def test-system
  (component/system-map
   :database (->TestDatabase "sqlite" ":memory:")))

(def test-system-interceptor
  (interceptor/on-request
   (fn [request]
     (assoc request :system (component/start test-system)))))

(def service
  (::bootstrap/service-fn (-> service/service
                              (server/attach-interceptor test-system-interceptor)
                              bootstrap/create-servlet)))

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
