(ns ipb-cpa.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [ipb-cpa.helper :as helpers]))

(reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(reg-sub
 :schedules
 (fn [db _]
   (:schedules db)))

(reg-sub
 :days-of-the-week
 (fn [db _]
   (:days-of-the-week db)))

(reg-sub
 :schedule/editing?
 (fn [db [_ schedule]]
   (if-let [editing? (get-in db [:editing-schedules (:id schedule)])]
     editing?
     false)))

(reg-sub
 :schedule/editing-errors
 (fn [db [_ schedule]]
   (when-let [errors (get-in db [:editing-schedules-errors (:id schedule)])]
     errors)))

(reg-sub
 :schedule/deleting?
 (fn [db [_ schedule]]
   (if-let [deleting? (get-in db [:deleting-schedules (:id schedule)])]
     deleting?
     false)))

(reg-sub
 :schedule/new-schedule
 (fn [db _]
   (:new-schedule db)))

(reg-sub
 :schedule/active-day
 (fn [db _]
   (helpers/active-tab (:days-of-the-week db))))

(reg-sub
 :schedule/events-of-day
 (fn [db [_ day]]
   (filter (fn [{:keys [day_of_the_week]}]
             (= day day_of_the_week))
           (:schedules db))))
