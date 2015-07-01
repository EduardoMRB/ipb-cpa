(ns ipb-cpa.admin-schedule
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [om.dom :as dom]
            [ajax.core :refer [GET POST DELETE PUT]]
            [cljs.core.async :as async :refer [chan put! <! >! alts!]]
            [clojure.string :as s]
            [cljs-time.core :as t]
            [cljs-time.format :as f]))

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
(declare sort-schedules)

(defn handler [resp]
  (swap! app-state assoc :schedules (sort-schedules resp)))

(defn err-handler [resp]
  (.log js/console "something went wrong" resp))

(GET "/api/schedule"
     {:handler handler
      :error-handler err-handler
      :response-format :json
      :keywords? true})

(defn persist-schedule [schedule success-c failure-c]
  (POST "/api/schedule"
        {:params {:schedule schedule}
         :handler #(put! success-c %)
         :error-handler #(put! failure-c %)
         :response-format :json
         :keywords? true}))

(defn destroy-schedule [schedule-id success-c failure-c]
  (DELETE (str "/api/schedule/" schedule-id)
          {:handler #(put! success-c %)
           :error-handler #(put! failure-c %)
           :response-format :json
           :keywords? true}))

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

;; Component functions
(defn delete-schedule
  "Deletes passed shedules from the app-state"
  [data schedule]
  (let [success-c (chan)
        failure-c (chan)
        _ (destroy-schedule (:id schedule) success-c failure-c)]
    (go
     (while true
       (let [[v c] (alts! [success-c failure-c])]
         (condp = c
           success-c
           (om/transact! data
                         :schedules
                         (fn [schedules]
                           (vec (remove (partial = schedule) schedules))))
           failure-c
           (.log js/console "Something bad happened" v)))))))

(defn add-schedule
  "Adds a new schedule to the database, in case of errors, nothing is done,
  otherwise, the id returned by the server is assoced into the new schedule and
  it's added to the app-state"
  [data args]
  (let [owner (:owner args)
        schedule (dissoc args :owner)
        success-c (chan)
        failure-c (chan)
        _ (persist-schedule schedule success-c failure-c)]
    (go
     (while true
       (let [[v c] (alts! [success-c failure-c])]
         (condp = c
           success-c
           (let [new-schedule (assoc schedule :id (:schedule-id v))]
             (om/transact! data :schedules #(vec (sort-schedules
                                                  (conj % new-schedule))))
             (om/set-state! owner :description "")
             (om/set-state! owner :time ""))
           failure-c
           (.log js/console "something went wrong" v)))))))

(defn handle-change [e key owner]
  (om/set-state! owner key (get-input-value (.-target e))))

(defn update-schedule
  "Updates a schedule with new description and time attributes mantaining the id
  and day_of_the_week fields as it is"
  [data args]
  (let [schedule   (-> args
                       (dissoc :owner))
        edit       (:edit args)]
    (om/transact! data
                  :schedules
                  (fn [schedules]
                    (let [scds (->> schedules
                                    (remove #(= (:id %) (:id schedule))))]
                      (vec (sort-schedules (conj scds schedule))))))
    (put! edit false)))

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
                          :days-of-the-week
                          (fn [dow]
                            (let [deactivated-dow (apply array-map
                                                         (flatten (map (fn [[day _]]
                                                                         [day false])
                                                                    dow)))]
                              (assoc deactivated-dow active-tab true)))))))))
   om/IRenderState
   (render-state [_ {:keys [active]}]
     (apply dom/ul #js {:className "tabs"}
            (om/build-all tab
                          (:days-of-the-week data)
                          {:init-state {:active active}})))))

(defn edit-schedule-line [schedule owner]
  (reify
   om/IInitState
   (init-state [_]
     {:description (:description schedule)
      :time (:time schedule)})
   om/IRenderState
   (render-state [_ {:keys [description time update edit]}]
     (dom/div #js {:className "large-12 columns"}
         (dom/div #js {:className "small-6 columns"}
           (dom/label nil "Nome"
             (dom/input #js {:type "text"
                             :ref "description"
                             :value description
                             :onChange #(handle-change % :description owner)})))
         (dom/div #js {:className "small-6 columns"}
           (dom/label nil "Horario"
             (dom/input #js {:type "text"
                             :ref "time"
                             :value time
                             :onChange #(handle-change % :time owner)})))
         (dom/div #js {:className "row"}
           (dom/div #js {:className "large-offset-8 large-4 columns"}
             (dom/button #js {:className "tiny"
                              :type "button"
                              :onClick #(put! update
                                              {:description (get-input-value (om/get-node owner "description"))
                                               :time (get-input-value (om/get-node owner "time"))
                                               :id (:id schedule)
                                               :day_of_the_week (:day_of_the_week schedule)
                                               :edit edit})}
               "Salvar")
             (dom/button #js {:className "tiny alert"
                              :onClick #(put! edit false)}
               "Cancelar")))))))

(defn delete-schedule-line [schedule owner]
  (reify
   om/IRenderState
   (render-state [_ {:keys [delete-intent delete]}]
     (dom/span nil "Deseja realmente remover essa programação? "
       (dom/button #js {:type "button"
                        :className "tiny alert"
                        :onClick #(put! delete schedule)}
         "Sim")
       (dom/button #js {:type "button"
                        :className "tiny"
                        :onClick #(put! delete-intent false)}
         "Não")))))

(defn schedule-line [schedule owner]
  (reify
   om/IInitState
   (init-state [_]
     {:editing? false
      :delete? false
      :edit (chan)
      :delete-intent (chan)})
   om/IWillMount
   (will-mount [_]
     (let [edit (om/get-state owner :edit)
           delete-intent (om/get-state owner :delete-intent)]
       (go (while true
             (let [[v c] (alts! [edit delete-intent])]
               (condp = c
                 edit
                 (om/set-state! owner :editing? v)
                 
                 delete-intent
                 (om/set-state! owner :delete? v)))))))
   om/IRenderState
   (render-state [_ {:keys [editing? delete? delete update edit delete-intent]}]
     (cond
       editing?
       (om/build edit-schedule-line schedule {:init-state {:update update
                                                           :edit edit}})
       delete?
       (om/build delete-schedule-line schedule {:init-state
                                                {:delete-intent delete-intent
                                                 :delete delete}})
       :else
       (dom/li nil (str (:description schedule) " - " (:time schedule) " ")
         (dom/button #js {:className "tiny"
                          :onClick #(om/set-state! owner :editing? true)}
           "Editar")
         (dom/button #js {:className "tiny alert"
                          ;; :onClick #(put! delete schedule)
                          :onClick #(om/set-state! owner :delete? true)
                          }
           "Remover"))))))

(defn schedule-list [{:keys [days-of-the-week schedules]} owner]
  (reify
   om/IInitState
   (init-state [_]
     {:description ""
      :time ""})
   om/IRenderState
   (render-state [_ {:keys [add delete update description time]}]
     (let [active-day (active-tab days-of-the-week)
           schedule-items (->> schedules
                               (filter (fn [{:keys [day_of_the_week]}]
                                         (= active-day day_of_the_week))))]
       (dom/div #js {:className "row"}
         (apply dom/ul nil
                (om/build-all schedule-line
                              schedule-items
                              {:init-state {:delete delete
                                            :update update
                                            :day active-day}}))
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

(defn schedule [data owner]
  (reify
   om/IInitState
   (init-state [_]
     {:add (chan)
      :delete (chan)
      :update (chan)})
   om/IWillMount
   (will-mount [_]
     (let [add (om/get-state owner :add)
           delete (om/get-state owner :delete)
           update (om/get-state owner :update)]
       (go (while true
             (let [[v c] (alts! [add delete update])]
               (condp = c
                 add (add-schedule data v)
                 delete (delete-schedule data v)
                 update (update-schedule data v)))))))
   om/IRenderState
   (render-state [_ {:keys [add delete update]}]
     (dom/div #js {:className "large-8 columns"}
       (dom/h2 nil "Schedule Component")
       (om/build tabs data)
       (om/build schedule-list
                 {:schedules (:schedules data)
                  :days-of-the-week (:days-of-the-week data)}
                 {:init-state {:add add
                               :delete delete
                               :update update}})))))

(om/root
 schedule
 app-state
 {:target (.querySelector js/document "#schedule-component")})
