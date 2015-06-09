(ns ipb-cpa.site.schedule
  (:require [ipb-cpa.db :as db]))

(defn- build-schedule-row [{:keys [day_of_the_week description time]}]
  (list
    [:h3 day_of_the_week]
    [:ul
     [:li [:em time] (str " - " description)]]))

(defn get-schedule-view []
  (if-let [schedules (seq (db/get-schedules nil))]
    (mapcat
      build-schedule-row
      schedules)))
