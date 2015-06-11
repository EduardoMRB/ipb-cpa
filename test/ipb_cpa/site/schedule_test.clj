(ns ipb-cpa.site.schedule-test
  (:require [midje.sweet :refer :all]
            [ipb-cpa.site.schedule :refer :all]
            [ipb-cpa.db :as db]))

(fact "db entries gets converted to a vector of vectors"
  (get-schedule-view [{:id 1
                       :day_of_the_week "Segunda"
                       :description "Desc"
                       :time "19h"}]) => (list
                                          [:h3 "Segunda"]
                                          [:ul
                                           (list
                                            [:li [:em "19h"] " - Desc"])]))

(fact "multiple schedules gets joined together"
  (get-schedule-view [{:id 1
                       :day_of_the_week "Segunda"
                       :description "Desc"
                       :time "19h"}
                      {:id 2
                       :day_of_the_week "Quarta"
                       :description "Another Desc"
                       :time "20:30h"}]) => (list
                                             [:h3 "Segunda"]
                                             [:ul
                                              (list
                                               [:li [:em "19h"] " - Desc"])]
                                             [:h3 "Quarta"]
                                             [:ul
                                              (list
                                               [:li [:em "20:30h"] " - Another Desc"])]))

(fact "returns nil if there is no schedule in database"
  (get-schedule-view []) => nil)

(fact "repeated days of the week get stacked up"
  (get-schedule-view [{:id 1
                       :day_of_the_week "Segunda"
                       :description "Desc"
                       :time "19:00h"}
                      {:id 3
                       :day_of_the_week "Segunda"
                       :description "Other desc"
                       :time "20:30h"}
                      {:id 2
                       :day_of_the_week "Quarta"
                       :description "Another Desc"
                       :time "20:30h"}]) => (list
                                             [:h3 "Segunda"]
                                             [:ul
                                              (list
                                               [:li [:em "19:00h"] " - Desc"]
                                               [:li [:em "20:30h"] " - Other desc"])]
                                             [:h3 "Quarta"]
                                             [:ul
                                              (list
                                               [:li [:em "20:30h"] " - Another Desc"])]))

(fact "repeated schedules gets sorted by time"
  (get-schedule-view [{:id 1
                       :day_of_the_week "Segunda"
                       :description "Desc"
                       :time "22:00h"}
                      {:id 3
                       :day_of_the_week "Segunda"
                       :description "Other desc"
                       :time "20:30h"}
                      {:id 2
                       :day_of_the_week "Quarta"
                       :description "Another Desc"
                       :time "20:30h"}]) => (list
                                             [:h3 "Segunda"]
                                             [:ul
                                              (list
                                               [:li [:em "20:30h"] " - Other desc"]
                                               [:li [:em "22:00h"] " - Desc"])]
                                             [:h3 "Quarta"]
                                             [:ul
                                              (list
                                               [:li [:em "20:30h"] " - Another Desc"])]))

(fact "repeated schedules gets sorted by day of the week"
  (get-schedule-view [{:id 2
                       :day_of_the_week "Quarta"
                       :description "Another Desc"
                       :time "20:30h"}
                      {:id 1
                       :day_of_the_week "Segunda"
                       :description "Desc"
                       :time "22:00h"}
                      {:id 3
                       :day_of_the_week "Segunda"
                       :description "Other desc"
                       :time "20:30h"}]) => (list
                                             [:h3 "Segunda"]
                                             [:ul
                                              (list
                                               [:li [:em "20:30h"] " - Other desc"]
                                               [:li [:em "22:00h"] " - Desc"])]
                                             [:h3 "Quarta"]
                                             [:ul
                                              (list
                                               [:li [:em "20:30h"] " - Another Desc"])]))
