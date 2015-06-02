(ns ipb-cpa.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [ipb-cpa.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(facts home-page-test
  (:headers (response-for service :get "/")) => {"Content-Type" "text/html;charset=UTF-8"
                                                 "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
                                                 "X-Frame-Options" "DENY"
                                                 "X-Content-Type-Options" "nosniff"
                                                 "X-XSS-Protection" "1; mode=block"})
