(ns ipb-cpa.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]))

(reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))
