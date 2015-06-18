(ns ipb-cpa.admin-schedule
  (:require [om.core :as om]
            [om.dom :as dom]))

(defn schedule [data owner]
  (reify
   om/IRender
   (render [_]
     (dom/h2 nil "Schedule Component"))))

(om/root
 schedule
 {}
 {:target (.getElementById js/document "schedule-component")})
