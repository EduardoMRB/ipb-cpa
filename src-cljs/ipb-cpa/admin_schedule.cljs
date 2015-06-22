(ns ipb-cpa.admin-schedule
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [om.dom :as dom]
            [ajax.core :refer [GET]]
            [cljs.core.async :as async :refer [chan put! <! >!]]))

(def app-state (atom {:schedules []
                      :active-tab "Seg"
                      :days-of-the-week [{:day "Seg" :active? true}
                                         {:day "Ter" :active? false}
                                         {:day "Quar" :active? false}
                                         {:day "Quin" :active? false}
                                         {:day "Sex" :active? false}
                                         {:day "Sab" :active? false}
                                         {:day "Dom" :active? false}]}))

;; Ajax stuff
(defn handler [resp]
  (swap! app-state assoc :schedules resp))

(defn err-handler [resp]
  (.log js/console "something went wrong" resp))

(GET "/api/schedule"
     {:handler handler
      :error-handler err-handler})

;; Om components
(defn tab [{:keys [day active?]} owner]
  (reify
   om/IRenderState
   (render-state [_ {:keys [active]}]
     (dom/li #js {:className (str "tab-title" (if active? " active"))}
       (dom/a #js {:onClick #(put! active day)}
              day)))))

(defn tabs [data owner]
  (reify
   om/IInitState
   (init-state [_]
     {:active (chan)
      :active-tab "Seg"})
   om/IWillMount
   (will-mount [_]
     (let [active (om/get-state owner :active)]
       (go
        (while true
          (let [active-tab (<! active)]
            (om/transact! data
                          (fn [app-state]
                            ;; TODO: Change app-state structure to a map
                            app-state)))))))
   om/IRenderState
   (render-state [_ {:keys [active]}]
     (apply dom/ul #js {:className "tabs"}
            (om/build-all tab
                          (:days-of-the-week data)
                          {:init-state {:active active}})))))

(defn schedule [data owner]
  (reify
   om/IRender
   (render [_]
     (dom/div nil
       (dom/h2 nil "Schedule Component")
       (om/build tabs data)))))

(om/root
 schedule
 app-state
 {:target (.getElementById js/document "schedule-component")})
