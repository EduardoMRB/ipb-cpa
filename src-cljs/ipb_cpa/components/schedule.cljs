(ns ipb-cpa.components.schedule
  (:require [ajax.core :refer [DELETE GET POST PUT]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [cljs.core.async :as async :refer [put!]]
            [clojure.string :as s]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [reagent.core :as r]
            [ipb-cpa.helper :as helpers]))

;; =============================================================================
;; Ajax stuff
;; =============================================================================

(declare sort-schedules)

(defn resource-call [f url success-c failure-c & params]
  (let [default-map {:handler #(put! success-c %)
                     :error-handler #(put! failure-c %)
                     :response-format :json
                     :keywords? true}
        params (if (seq params) (first params))
        options (if params (assoc default-map :params params) default-map)]
    (f url options)))

(defn persist-schedule [schedule success-c failure-c]
  (resource-call POST "/api/schedule" success-c failure-c {:schedule schedule}))

(defn destroy-schedule [schedule-id success-c failure-c]
  (resource-call DELETE (str "/api/schedule/" schedule-id) success-c failure-c))

(defn put-schedule [schedule success-c failure-c]
  (resource-call PUT
                 (str "/api/schedule/" (:id schedule))
                 success-c
                 failure-c
                 {:schedule schedule}))

;; =============================================================================
;; Util functions
;; =============================================================================

(defn tab-name [tab-keyword]
  (s/capitalize (s/replace (str tab-keyword) ":" "")))

(defn active-tab [days-of-week]
  (let [mappings {:seg "Segunda" :ter "Terça" :quar "Quarta" :quin "Quinta"
                  :sex "Sexta" :sab "Sábado" :dom "Domingo"}]
    (->> days-of-week
         (filter (fn [[_ active?]]
                   active?))
         (ffirst)
         (mappings))))

;; =============================================================================
;; Validations
;; =============================================================================

(defn validate-schedule
  "Validates a schedule and returns a vector where the first element contains
  the validation errors or nil if the schedule is valid and the second element
  is the updated schedules with or without errors inside the key
  :bouncer.core/errors.

  A valid schedule consist in a key :description that is required and the :time
  which has the format of HH:MMh"
  [schedule]
  (b/validate schedule
              :description [[v/required :message "A programação precisa ter um nome"]]
              :time [[v/matches #"\d{2}:\d{2}h" :message "O horário precisa ter o formato: 13:30h"]]))

;; ;; =============================================================================
;; ;; Components
;; ;; =============================================================================
;; (defn delete-schedule
;;   "Deletes passed shedules from the app-state"
;;   [data schedule]
;;   (let [success-c (chan)
;;         failure-c (chan)
;;         _ (destroy-schedule (:id schedule) success-c failure-c)]
;;     (go
;;      (while true
;;        (let [[v c] (alts! [success-c failure-c])]
;;          (condp = c
;;            success-c
;;            (om/transact! data
;;                          :schedules
;;                          (fn [schedules]
;;                            (vec (remove (partial = schedule) schedules))))
;;            failure-c
;;            (.log js/console "Something bad happened" v)))))))

;; (defn reset-schedule-list-state! [owner]
;;   (om/set-state! owner :description "")
;;   (om/set-state! owner :time "")
;;   (om/set-state! owner :errors {}))

;; (defn add-schedule
;;   "Adds a new schedule to the database, in case of errors, nothing is done,
;;   otherwise, the id returned by the server is assoced into the new schedule and
;;   it's added to the app-state"
;;   [data args]
;;   (let [owner (:owner args)
;;         schedule (dissoc args :owner)
;;         [errors schedule] (validate-schedule schedule)]
;;     (cond
;;       (seq errors) ;; Validation errors occurs.
;;       (om/set-state! owner :errors errors)

;;       :else ;; Schedule is valid, proceed.
;;       (let [success-c (chan)
;;             failure-c (chan)
;;             _ (persist-schedule schedule success-c failure-c)]
;;         (go
;;          (while true
;;            (let [[v c] (alts! [success-c failure-c])]
;;              (condp = c
;;                success-c
;;                (let [new-schedule (assoc schedule :id (:schedule-id v))]
;;                  ;; Add the database generated id into the schedule map and put
;;                  ;; it into the app-state
;;                  (om/transact! data :schedules #(vec (sort-schedules
;;                                                       (conj % new-schedule))))
;;                  (reset-schedule-list-state! owner))
;;                failure-c
;;                (.log js/console "something went wrong" v)))))))))

;; (defn handle-change [e key owner]
;;   (om/set-state! owner key (get-input-value (.-target e))))

;; (defn update-schedule
;;   "Updates a schedule with new description and time attributes mantaining the id
;;   and day_of_the_week fields as it is"
;;   [data args]
;;   (let [schedule   (-> args
;;                        (dissoc :owner)
;;                        (dissoc :edit))
;;         edit       (:edit args)
;;         owner      (:owner args)
;;         [errors _] (validate-schedule schedule)]
;;     (cond
;;       (seq errors)
;;       (om/set-state! owner :errors errors)

;;       :else
;;       (let [success-c  (chan)
;;             failure-c  (chan)
;;             _          (put-schedule schedule success-c failure-c)]
;;         (go
;;          (while true
;;            (let [[v c] (alts! [success-c failure-c])]
;;              (condp = c
;;                success-c
;;                (do
;;                  (om/transact! data
;;                                :schedules
;;                                (fn [schedules]
;;                                  (let [scds (->> schedules
;;                                                  (remove #(= (:id %) (:id schedule))))]
;;                                    (vec (sort-schedules (conj scds schedule))))))
;;                  (put! edit false))

;;                failure-c
;;                (.log js/console "Something went wrong" v)))))))))

(defn tab [day active?]
  [:li.tab-title {:class-name (if active? "active")}
   [:a {:on-click #(dispatch [:schedule/set-active-tab day])}
    (tab-name day)]])

(defn tabs [days-of-the-week]
  [:ul.tabs
   (for [[day active?] days-of-the-week]
     ^{:key day}
     [tab day active?])])

(defn error-message [errors k]
  (if-let [[err-msg] (errors k)]
    [:small.error err-msg]))

(defn edit-schedule-line [schedule]
  (let [local-schedule (r/atom schedule)]
    (fn []
      [:div.large-12.columns
       [:div.small-6.columns
        [:label "Nome"]
        [:input {:type :text
                 :value (:description @local-schedule)
                 :on-change #(swap! local-schedule assoc :description (helpers/get-target-value %))}]]

       [:div.small-6.columns
        [:label "Horário"]
        [:input {:type :text
                 :value (:time @local-schedule)
                 :on-change #(swap! local-schedule assoc :time (helpers/get-target-value %))}]]

       [:div.row
        [:div.large-offset-8.large-4.columns
         [:button.tiny {:type :button
                        :on-click #(dispatch [:schedule/put-schedule @local-schedule])}
          "Salvar"]
         [:button.tiny.alert {:type :button
                              :on-click #(dispatch [:schedule/set-editing (:id schedule) false])}
          "Cancelar"]]]])))

(defn delete-schedule-line [schedule]
  [:span "Deseja realmente remover essa programação?"
   [:button.tiny.alert {:type :button
                        :on-click #(dispatch [:schedule/delete-schedule schedule])}
    "Sim"]
   [:button.tiny.alert {:type :button
                        :on-click #(dispatch [:schedule/set-deleting (:id schedule) false])}
    "Não"]])

(defn schedule-line [schedule]
  (let [editing? (subscribe [:schedule/editing? schedule])
        delete?  (subscribe [:schedule/deleting? schedule])]
    (fn []
      (cond
        @editing?
        [edit-schedule-line schedule]

        @delete?
        [delete-schedule-line schedule]

        :else
        [:li
         (str (:description schedule) " - " (:time schedule))
         [:button.tiny {:on-click #(dispatch [:schedule/set-editing (:id schedule) true])}
          "Editar"]
         [:button.tiny.alert {:on-click #(dispatch [:schedule/set-deleting (:id schedule) true])}
          "Remover"]]))))

(defn schedule-list []
  (let [schedules        (subscribe [:schedules])
        days-of-the-week (subscribe [:days-of-the-week])]
    (fn []
      (let [active-day     (active-tab @days-of-the-week)
            schedule-items (->> @schedules
                                (filter (fn [{:keys [day_of_the_week]}]
                                          (= active-day day_of_the_week))))]
        [:div.row
         [:ul.no-bullet
          (for [schedule-item schedule-items]
            ^{:key (:time schedule-item)}
            [schedule-line schedule-item])]

         [:form
          [:fieldset
           [:legend "Inserir programação"]
           [:div.row
            [:div.large-6.columns
             [:div.row.collapse.prefix-radius
              [:div.small-3.columns
               [:span.prefix "Nome"]]
              [:div.small-9.columns
               [:input {:type :text
                        :on-change #(.log js/console "Changing text here")}]]]]

            [:div.large-6.columns
             [:div.row.collapse.prefix-radius
              [:div.small-9.columns
               [:input {:type :text
                        :on-change #(.log js/console "Changing text here")}]]
              [:div.small-3.columns
               [:span.postfix "Horário"]]]]]

           [:div.large-2.large-offset-10.columns
            [:button.small {:type :button
                            :on-click #(.log js/console "Clicking desperately on this button")}
             "Criar"]]]]]))))

(defn schedule-panel []
  (let [schedules (subscribe [:schedules])
        days-of-the-week (subscribe [:days-of-the-week])]
    (fn []
      [:div.large-8.columns
       [:h1 "Programação"]
       [tabs @days-of-the-week]
       [schedule-list @schedules @days-of-the-week]])))
