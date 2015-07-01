(ns ipb-cpa.db
  (:require [yesql.core :refer [defqueries]]))

(defqueries "ipb_cpa/sql/schedule.sql")

(defn- as-vector
  ([{:keys [day_of_the_week description time]}]
   [day_of_the_week description time])
  ([schedule id]
   (conj (as-vector schedule) id)))

(def get-schedules select-all-schedules)

(defn get-schedule [db schedule-id]
  (first (select-schedule db schedule-id)))

(defn add-schedule! [db schedule]
  (apply insert-schedule! db (as-vector schedule)))

(defn add-schedule<! [db schedule]
  (let [res (apply insert-schedule<! db (as-vector schedule))]
    (second (first res))))

(defn modify-schedule! [db schedule-id new-schedule]
  (apply update-schedule! db (as-vector new-schedule schedule-id)))

(def remove-schedule! delete-schedule!)
