(ns ipb-cpa.events
  (:require [ipb-cpa.db :as db]
            [re-frame.core :as rf :refer [reg-event-db]]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/app-db))

(reg-event-db
 :set-active-panel
 (fn [db [_ panel]]
   (assoc db :active-panel panel)))

