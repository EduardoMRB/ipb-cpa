(ns ipb-cpa.site.schedule
  (:require [ipb-cpa.db :as db]
            [clj-time.format :as f]))

(def ^:private days-of-the-week
  ["Segunda" "Terça" "Quarta" "Quinta" "Sexta" "Sábado" "Domingo"])

(defn- sort-events [events]
  (sort (fn [a b]
          (let [fmt (f/formatter "H:m'h")
                a-date (f/parse fmt (:time a))
                b-date (f/parse fmt (:time b))]
            (compare a-date b-date)))
        events))

(defn- sort-days-of-week [schedule]
  (sort (fn [a b]
          (let [a-day (first a)
                b-day (first b)]
            (compare (.indexOf days-of-the-week a-day)
                     (.indexOf days-of-the-week b-day))))
        schedule))

(defn- transform-schedules [schedules]
  (let [xformed (reduce (fn [xfrmd schedule]
                          (update-in xfrmd
                                     [(:day_of_the_week schedule)]
                                     #(if (seq %)
                                        (sort-events (conj % schedule))
                                        [schedule])))
                        {}
                        schedules)]
    (sort-days-of-week xformed)))

(defn- build-schedule-row [[day_of_the_week schedules]]
  (list
   [:h3 day_of_the_week]
   [:ul
    (for [{:keys [time description]} schedules]
         [:li [:em time] (str " - " description)])]))

(defn get-schedule-view []
  (if-let [schedules (seq (db/get-schedules nil))]
    (mapcat
     build-schedule-row
     (transform-schedules schedules))))
