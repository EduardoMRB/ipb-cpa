(ns ipb-cpa.admin-schedule
  (:require [om.core :as om]
            [om.dom :as dom]
            [ajax.core :refer [GET]]))

(def app-state (atom {:schedules []}))

;; Ajax stuff
(defn handler [resp]
  (swap! app-state assoc :schedules resp))

(defn err-handler [resp]
  (.log js/console "something went wrong" resp))

(GET "/api/schedule"
     {:handler handler
      :error-handler err-handler})

;; Om components
(defn tabs [_ _]
  (let [days-of-the-week ["Seg" "Ter" "Quar" "Quin" "Sex" "Sab" "Dom"]]
    (reify
     om/IRender
     (render [_]
       (apply dom/ul nil (map (partial dom/li nil) days-of-the-week))))))

(defn schedule [data owner]
  (reify
   om/IRender
   (render [_]
     (dom/div nil
       (dom/h2 nil "Schedule Component")
       (om/build tabs data)))))

(om/root
 schedule
 {}
 {:target (.getElementById js/document "schedule-component")})
