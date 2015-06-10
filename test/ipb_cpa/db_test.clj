(ns ipb-cpa.db-test
  (:require [midje.sweet :refer :all]
            [ipb-cpa.db :refer :all]
            [yesql.core :refer [defqueries]]
            [clojure.java.jdbc :refer [with-db-transaction]]))

(def memory-db {:subprotocol "sqlite"
                :subname ":memory:"})
(def schedule {:day_of_the_week "Segunda"
               :description "Escola dominical, wait, wat?"
               :time "20h"})

(fact "an empty table will return no schedules"
  (with-db-transaction
    [db memory-db]
    (create-schedule-table! db)
    (get-schedules db)) => [])

(fact "we can insert schedule"
  (with-db-transaction
    [db memory-db]
    (create-schedule-table! db)
    (add-schedule! db schedule)
    (get-schedules db)) => [(assoc schedule :id 1)])

(fact "we can update an existing schedule"
  (let [updated-schedule (assoc schedule :day_of_the_week "Domingo")]
    (with-db-transaction
      [db memory-db]
      (create-schedule-table! db)
      (add-schedule! db schedule)
      (modify-schedule! db 1 updated-schedule)
      (get-schedules db)) => [(assoc updated-schedule :id 1)]))

(fact "we can get a schedule by id"
  (with-db-transaction
    [db memory-db]
    (create-schedule-table! db)
    (add-schedule! db schedule)
    (get-schedule db 1)) => (assoc schedule :id 1))