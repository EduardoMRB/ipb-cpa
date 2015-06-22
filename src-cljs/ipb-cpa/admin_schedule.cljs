(ns ipb-cpa.admin-schedule
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [om.dom :as dom]
            [ajax.core :refer [GET]]
            [cljs.core.async :as async :refer [chan put! <! >!]]
            [clojure.string :as s]))

(enable-console-print!)

(def app-state (atom {:schedules []
                      :days-of-the-week (array-map
                                         :seg true
                                         :ter false
                                         :quar false
                                         :quin false
                                         :sex false
                                         :sab false
                                         :dom false)}))

;; Ajax stuff
(defn handler [resp]
  (swap! app-state assoc :schedules resp))

(defn err-handler [resp]
  (.log js/console "something went wrong" resp))

(GET "/api/schedule"
     {:handler handler
      :error-handler err-handler
      :response-format :json
      :keywords? true})

;; Util functions
(defn tab-name [tab-keyword]
  (s/capitalize (s/replace (str tab-keyword) ":" "")))

;; Om components
(defn tab [[day active?] owner]
  (reify
   om/IRenderState
   (render-state [_ {:keys [active]}]
     (dom/li #js {:className (str "tab-title" (if active? " active"))}
       (dom/a #js {:onClick #(put! active day)}
              (tab-name day))))))

(defn tabs [data owner]
  (reify
   om/IInitState
   (init-state [_]
     {:active (chan)})
   om/IWillMount
   (will-mount [_]
     (let [active (om/get-state owner :active)]
       (go
        (while true
          (let [active-tab (<! active)]
            (om/transact! data
                          (fn [app-state]
                            (let [deactivated-dow (apply array-map
                                                         (flatten (map (fn [[day _]]
                                                                         [day false])
                                                                       (:days-of-the-week app-state))))
                                  new-dow (assoc deactivated-dow active-tab true)]
                              (assoc app-state :days-of-the-week new-dow)))))))))
   om/IRenderState
   (render-state [_ {:keys [active]}]
     (apply dom/ul #js {:className "tabs"}
            (om/build-all tab
                          (:days-of-the-week data)
                          {:init-state {:active active}})))))

(defn schedule-line [schedule owner]
  (reify
   om/IRender
   (render [_]
     (dom/li nil (str (:description schedule) " - " (:time schedule) " ")
             (apply dom/ul #js {:className "button-group"}
                    (dom/button #js {:className "tiny"} "Editar")
                    (dom/button #js {:className "tiny alert"} "Remover"))))))

(defn schedule-list [data owner]
  (reify
   om/IRender
   (render [_]
     (let [mappings {:seg "Segunda" :ter "Terça" :quar "Quarta" :quin "Quinta"
                     :sex "Sexta" :sab "Sábado" :dom "Domingo"}
           active-tab (->> (:days-of-the-week data)
                           (filter (fn [[_ active?]]
                                     active?))
                           (ffirst)
                           (mappings))
           schedule-items (->> (:schedules data)
                               (filter (fn [{:keys [day_of_the_week]}]
                                         (= active-tab day_of_the_week))))]
       (apply dom/ul nil
              (om/build-all schedule-line schedule-items))))))

(defn schedule [data owner]
  (reify
   om/IRender
   (render [_]
     (dom/div nil
       (dom/h2 nil "Schedule Component")
       (om/build tabs data)
       (om/build schedule-list data)))))

(om/root
 schedule
 app-state
 {:target (.querySelector js/document "#schedule-component")})
