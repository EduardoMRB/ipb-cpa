(ns ipb-cpa.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]))

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
 :schedule/deleting?
 (fn [db [_ schedule]]
   (if-let [deleting? (get-in db [:deleting-schedules (:id schedule)])]
     deleting?
     false)))
