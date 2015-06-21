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

;; Behavior functions

;; Om components
(defn tab [day owner]
  (reify
   om/IRenderState
   (render-state [_ {:keys [active-tab]}]
     (dom/li #js {:className (str "tab-title" (if (= active-tab day) " active"))}
       (dom/a #js {:onClick #(.log js/console "clicked")}
              day)))))

(defn tabs [_ _]
  (let [days-of-the-week ["Seg" "Ter" "Quar" "Quin" "Sex" "Sab" "Dom"]]
    (reify
     om/IInitState
     (init-state [_]
       {:active-tab "Seg"})
     om/IRenderState
     (render-state [_ {:keys [active-tab]}]
       (apply dom/ul #js {:className "tabs"}
              (om/build-all tab
                            days-of-the-week
                            {:init-state {:active-tab active-tab}}))))))

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
