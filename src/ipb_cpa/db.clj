(ns ipb-cpa.db
  (:require [yesql.core :refer [defqueries]]))

(defqueries "ipb_cpa/sql/schedule.sql")

(defn get-schedules [db]
  (select-all-schedules {} {:connection db}))

(defn get-schedule [db schedule-id]
  (select-schedule {:id schedule-id} {:connection db :result-set-fn first}))

(defn add-schedule! [db schedule]
  (insert-schedule! schedule {:connection db}))

(defn add-schedule<! [db schedule]
  (let [res (insert-schedule<! schedule {:connection db})]
    (second (first res))))

(defn modify-schedule! [db schedule-id new-schedule]
  (update-schedule! (assoc new-schedule :id schedule-id) {:connection db}))

(defn remove-schedule! [db id]
  (delete-schedule! {:id id} {:connection db}))
