(ns ipb-cpa.view.home
  (:require [ipb-cpa.site.daily-verse :refer [get-verse]]
            [ipb-cpa.site.schedule :as schedule]
            [ipb-cpa.db :as db]
            [ipb-cpa.view.layout :as layout]))

(defn random-verses []
  [:div.small-12.large-6.columns
   [:div.panel
    [:h2 "Versículo do dia"]
    [:p (:text (get-verse))]
    [:em (:reference (get-verse))]]])

(defn weekly-schedule [database]
  [:div.small-12.large-6.columns
   [:div.panel
    [:h2 "Programação Semanal"]
    (schedule/get-schedule-view (db/get-schedules database))]])

(defn home-first-row [db]
  [:div.columns
   (weekly-schedule db)
   (random-verses)])

(defn index [url-for db]
  (layout/default-template url-for (home-first-row db) (layout/footer-map)))
