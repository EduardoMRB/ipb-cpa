(ns ipb-cpa.admin-schedule
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [om.dom :as dom]
            [ajax.core :refer [GET]]
            [cljs.core.async :as async :refer [chan put! <! >!]]
            [clojure.string :as s]
            (cljs-time.core :as t)
            [cljs-time.format :as f])
  (:import goog.date.Date))

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

(defn get-input-value [input]
  (.-value input))

(defn active-tab [days-of-week]
  (let [mappings {:seg "Segunda" :ter "Terça" :quar "Quarta" :quin "Quinta"
                  :sex "Sexta" :sab "Sábado" :dom "Domingo"}]
    (->> days-of-week
         (filter (fn [[_ active?]]
                   active?))
         (ffirst)
         (mappings))))

(defn sort-schedules
  "Sort schedules by time in ascending order."
  [schedules]
  (sort (fn [a b]
          (let [fmt (f/formatters :hour-minute)
                a-time (f/parse fmt (:time a))
                b-time (f/parse fmt (:time b))]
            (cond (t/after? a-time b-time) 1
                  (t/after? b-time a-time) -1
                  :else 0)))
        schedules))

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
       (dom/button #js {:className "tiny"} "Editar")
       (dom/button #js {:className "tiny alert"} "Remover")))))

(defn handle-change [e key owner]
  (om/set-state! owner key (get-input-value (.-target e))))

(defn schedule-list [{:keys [days-of-the-week schedules]} owner]
  (reify
   om/IInitState
   (init-state [_]
     {:description ""
      :time ""})
   om/IRenderState
   (render-state [_ {:keys [add description time]}]
     (let [active-day (active-tab days-of-the-week)
           schedule-items (->> schedules
                               (filter (fn [{:keys [day_of_the_week]}]
                                         (= active-day day_of_the_week))))]
       (dom/div #js {:className "row"}
         (apply dom/ul nil
                (om/build-all schedule-line schedule-items))
         (dom/form nil
           (dom/fieldset nil
             (dom/legend nil "Inserir programação")
             (dom/div #js {:className "row"}
               (dom/div #js {:className "large-6 columns"}
                 (dom/div #js {:className "row collapse prefix-radius"}
                   (dom/div #js {:className "small-3 columns"}
                     (dom/span #js {:className "prefix"} "Nome"))
                   (dom/div #js {:className "small-9 columns"}
                     (dom/input #js {:type "text"
                                     :ref "schedule-name"
                                     :value description
                                     :onChange #(handle-change % :description owner)}))))
               (dom/div #js {:className "large-6 columns"}
                 (dom/div #js {:className "row collapse postfix-radius"}
                   (dom/div #js {:className "small-9 columns"}
                     (dom/input #js {:type "text"
                                     :ref "schedule-time"
                                     :value time
                                     :onChange #(handle-change % :time owner)}))
                   (dom/div #js {:className "small-3 columns"}
                     (dom/span #js {:className "postfix"}
                       "Horário")))))
             (dom/div #js {:className "large-2 large-offset-10 columns"}
               (dom/button #js {:className "small"
                                :type "button"
                                :ref "new-schedule"
                                :onClick #(put! add
                                                {:description (get-input-value (om/get-node owner "schedule-name"))
                                                 :time (get-input-value (om/get-node owner "schedule-time"))
                                                 :day_of_the_week (active-tab days-of-the-week)
                                                 :owner owner})}
                 "Criar")))))))))

(defn add-schedule [data args]
  (let [owner (:owner args)
        schedule (dissoc args :owner)]
  (om/transact! data :schedules #(sort-schedules (conj % schedule)))
  (om/set-state! owner :description "")
  (om/set-state! owner :time "")))

(defn schedule [data owner]
  (reify
   om/IInitState
   (init-state [_]
     {:add (chan)})
   om/IWillMount
   (will-mount [_]
     (let [add (om/get-state owner :add)]
       (go (while true
             (let [args (<! add)]
               (add-schedule data args))))))
   om/IRenderState
   (render-state [_ {:keys [add]}]
     (dom/div #js {:className "large-8 columns"}
       (dom/h2 nil "Schedule Component")
       (om/build tabs data)
       (om/build schedule-list
                 {:schedules (:schedules data)
                  :days-of-the-week (:days-of-the-week data)}
                 {:init-state {:add add}})))))

(om/root
 schedule
 app-state
 {:target (.querySelector js/document "#schedule-component")})
