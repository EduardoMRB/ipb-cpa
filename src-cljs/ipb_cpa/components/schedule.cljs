(ns ipb-cpa.components.schedule
  (:require [ajax.core :refer [DELETE GET POST PUT]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [cljs.core.async :as async :refer [put!]]
            [clojure.string :as s]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [reagent.core :as r]
            [ipb-cpa.helper :as helpers]))

;; ------------------------
;; Styles
;; ------------------------

(def styles
  {:tab {:border-color "#ccc"
         :width "14.27%"}

   :tab-inactive {:border-width 1
                  :border-style :solid
                  :border-color "#ccc"
                  :border-bottom-color "#ccc"}

   :tab-active {:border-width "1px 1px 4px 1px"
                :border-style :solid
                :border-bottom-color "#00a4df"}

   :tab-name {:color "#96a9b9"
              :text-transform :uppercase
              :text-align :center
              :font-size 12
              :align-self :center}

   :badge {:border-radius "10%"
           :background-color "#767676"
           :color "#fefefe"
           :padding 5
           :font-align :center
           :font-size 12}})

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

;; =============================================================================
;; Components
;; =============================================================================

(defn tab [day active?]
  [:li.tab-title {:class-name (if active? "active")
                  :style (merge (:tab styles)
                                (if active?
                                  (:tab-active styles)
                                  (:tab-inactive styles)))}
   [:a {:style (merge {:background-color (if active?
                                           "#fff"
                                           "#f8f8f8")}
                      (:tab-name styles))
        :on-click #(dispatch [:schedule/set-active-tab day])}
    (helpers/tab-name day)]])

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
                 :auto-focus true
                 :on-key-press (helpers/on-enter #(dispatch [:schedule/put-schedule @local-schedule]))
                 :value (:description @local-schedule)
                 :on-change #(swap! local-schedule assoc :description (helpers/get-target-value %))}]]

       [:div.small-6.columns
        [:label "Horário"]
        [:input {:type :text
                 :value (:time @local-schedule)
                 :on-key-press (helpers/on-enter #(dispatch [:schedule/put-schedule @local-schedule]))
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
   [:button.tiny {:type :button
                  :on-click #(dispatch [:schedule/set-deleting (:id schedule) false])}
    "Não"]])

(defn schedule-line [schedule]
  (let [editing? (subscribe [:schedule/editing? schedule])
        delete?  (subscribe [:schedule/deleting? schedule])]
    (cond
      @editing?
      [edit-schedule-line schedule]

      @delete?
      [delete-schedule-line schedule]

      :else
      [:div.large-12.columns
       [:div.large-4.columns
        [:span (:description schedule)]
        " "
        [:span {:style (:badge styles)}
         (:time schedule)]]
       [:ul.button-group.radius
        [:li
         [:button.tiny {:on-click #(dispatch [:schedule/set-editing (:id schedule) true])}
          "Editar"]]
        [:li
         [:button.tiny.alert {:on-click #(dispatch [:schedule/set-deleting (:id schedule) true])}
          "Remover"]]]])))

(defn new-schedule-form [dow]
  (let [new-schedule    (subscribe [:schedule/new-schedule])
        active-day      (subscribe [:schedule/active-day])
        dispatch-create #(dispatch [:schedule/create @active-day])]
    [:form
     [:fieldset
      [:legend "Inserir programação"]
      [:div.large-4.columns
       [:div.row.collapse.prefix-radius
        [:div.small-3.columns
         [:span.prefix "Nome"]]
        [:div.small-9.columns
         [:input {:type      :text
                  :value     (:description @new-schedule)
                  :on-change #(dispatch [:schedule/set-new :description (helpers/get-target-value %)])}]]]]

      [:div.large-4.columns
       [:div.row.collapse.prefix-radius
        [:div.small-9.columns
         [:input {:type         :text
                  :value        (:time @new-schedule)
                  :on-key-press (helpers/on-enter dispatch-create)
                  :on-change    #(dispatch [:schedule/set-new :time (helpers/get-target-value %)])}]]
        [:div.small-3.columns
         [:span.postfix "Horário"]]]]

      [:div.large-4.columns
       [:button.tiny {:type     :button
                      :on-click dispatch-create}
        "Criar"]]]]))

(defn schedule-list []
  (let [active-day     (subscribe [:schedule/active-day])
        schedule-items (subscribe [:schedule/events-of-day @active-day])]
    [:div
     [new-schedule-form]
     [:div
      (for [schedule-item @schedule-items]
        ^{:key (:id schedule-item)}
        [schedule-line schedule-item])]]))

(defn schedule-panel []
  (let [days-of-the-week (subscribe [:days-of-the-week])]
    [:div.large-12.columns
     [:h1 "Programação "
      [:small "gerencie as informações que aparecem no site."]]
     [tabs @days-of-the-week]
     [schedule-list]]))
