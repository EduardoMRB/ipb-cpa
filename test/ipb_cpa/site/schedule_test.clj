(ns ipb-cpa.site.schedule-test
  (:require [midje.sweet :refer :all]
            [ipb-cpa.site.schedule :refer :all]
            [ipb-cpa.db :as db]))

(fact "db entries gets converted to a vector of vectors"
  (with-redefs [db/get-schedules (fn [_]
                                   [{:id 1
                                     :day_of_the_week "Segunda"
                                     :description "Desc"
                                     :time "19h"}])]
    (get-schedule-view)) => (list
                              [:h3 "Segunda"]
                              [:ul
                               [:li [:em "19h"] " - Desc"]]))

(fact "multiple schedules gets joined together"
  (with-redefs [db/get-schedules (fn [_]
                                   [{:id 1
                                     :day_of_the_week "Segunda"
                                     :description "Desc"
                                     :time "19h"}
                                    {:id 2
                                     :day_of_the_week "Quarta"
                                     :description "Another Desc"
                                     :time "20:30h"}])]
    (get-schedule-view)) => (list
                              [:h3 "Segunda"]
                              [:ul
                               [:li [:em "19h"] " - Desc"]]
                              [:h3 "Quarta"]
                              [:ul
                               [:li [:em "20:30h"] " - Another Desc"]]))

(fact "returns nil if there is no schedule in database"
  (with-redefs [db/get-schedules (fn [_][])]
    (get-schedule-view)) => nil)
