(ns ipb-cpa.service-test
  (:require [cheshire.core :as json]
            [com.stuartsierra.component :as component]
            [io.pedestal
             [http :as bootstrap]
             [test :refer :all]]
            [io.pedestal.interceptor.helpers :as interceptor]
            [ipb-cpa
             [server :as server]
             [service :as service]
             [test-system :as test-system]]
            [midje.sweet :refer :all]))

;; Test system.
(def test-system-interceptor
  (interceptor/on-request
   (fn [request]
     (assoc request :system (component/start (test-system/test-system))))))

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
      (get-in cont-resp [:headers "Content-Type"]) => (contains "text/html"))))

(facts "contact send message"
  (let [body "name=Eduardo+Borges&email=eduardomrb%40gmail.com&message=Hello"
        headers {"Content-Type" "application/x-www-form-urlencoded"}
        send-resp (response-for service :post "/contato" :body body :headers headers)]

    (fact "When email is sent, redirects to home-page"

      (:status send-resp) => 302

      @test-system/last-message => {:to "pastor@gmail.com"
                                    :from "eduardomrb@gmail.com"
                                    :subject "Contato do site IPB CPA IV - Eduardo Borges"
                                    :body "Hello"}

      (get-in send-resp [:headers "Location"]) => "/contato")))

(facts "admin"
  (let [resp (response-for service :get "/admin")]
    (fact "dashboard page is html"
      (get-in resp
              [:headers "Content-Type"]) => "text/html;charset=UTF-8")
    (fact "Dashboard page contains the word 'Bem Vindo!'"
      (:body resp) => (contains "Bem Vindo!"))))

(facts "admin-login"
       (let [resp (response-for service :get "/admin/login")]
         (fact "admin login page is html"
               (get-in resp [:headers "Content-Type"]) => "text/html;charset=UTF-8")
         (fact "login page contains the word Login"
               (:body resp) => (contains "Login"))))

(facts schedule-json-api

  (let [resp (response-for service :get "/api/schedule")]
    (fact "handler returns json"
      (get-in resp [:headers "Content-Type"]) => "application/json;charset=UTF-8")
    (fact "returns schedules in body"
      (:body resp) => (json/generate-string [{:id 1 :day_of_the_week "Domingo" :description "Escola biblica dominical" :time "08:30h"}
                                             {:id 2 :day_of_the_week "Domingo" :description "Culto de adoracao" :time "19:00h"}
                                             {:id 3 :day_of_the_week "Quinta" :description "Reuniao de oracao" :time "19:30h"}]))))

(facts admin-videos-page
  (fact "content-type is html"
    (get-in (response-for service :get "/admin/video")
            [:headers "Content-Type"]) => "text/html;charset=UTF-8"))
