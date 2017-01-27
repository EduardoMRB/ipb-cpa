(ns ipb-cpa.events.schedule
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [com.rpl.specter :as specter :refer [setval ALL]]))

(def day-kw->day
  {:seg  "Segunda"
   :ter  "Terça"
   :quar "Quarta"
   :quin "Quinta"
   :sex  "Sexta"
   :sab  "Sábado"
   :dom  "Domingo"})

(reg-event-db
 :set-schedules
 (fn [db [_ schedules]]
   (assoc db :schedules schedules)))

(reg-event-fx
 :schedule/set-active-tab
 (fn [{:keys [db]} [_ day]]
   {:db (let [all-inactive-dow (into (array-map)
                                     (map (fn [[k v]]
                                            [k false])
                                          (:days-of-the-week db)))]
          (assoc db :days-of-the-week (assoc all-inactive-dow day true)))
    :dispatch [:schedule/set-new :day_of_the_week (day-kw->day day)]}))

(reg-event-fx
 :schedule/put-schedule
 (fn [{:keys [db]} [_ schedule]]
   {:db db
    :http-xhrio {:method :put
                 :uri (str "/api/schedule/" (:id schedule))
                 :params schedule
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords true})
                 :on-success [:schedule/updated schedule]
                 :on-failure [:schedule/error-on-update]}}))

(reg-event-db
 :schedule/error-on-update
 (fn [db [_ result]]
   (.alert js/window "Oops, não foi possível atualizar a programação")
   db))

(reg-event-fx
 :schedule/updated
 (fn [{:keys [db]} [_ schedule _]]
   {:db (setval [:schedules ALL #(= (:id schedule) (:id %))]
                schedule
                db)
    :dispatch [:schedule/set-editing (:id schedule) false]}))

(reg-event-db
 :schedule/set-editing
 (fn [db [_ id visibility]]
   (assoc-in db [:editing-schedules id] visibility)))

(reg-event-fx
 :schedule/delete-schedule
 (fn [{:keys [db]} [_ schedule]]
   {:db db
    :http-xhrio {:method :delete
                 :uri (str "/api/schedule/" (:id schedule))
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords true})
                 :on-success [:schedule/deleted schedule]
                 :on-failure [:schedule/error-on-delete]}}))

(reg-event-db
 :schedule/error-on-delete
 (fn [db [_ result]]
   (.alert js/window "Oops, não foi possível remover a programação")
   db))

(reg-event-fx
 :schedule/deleted
 (fn [{:keys [db]} [_ schedule _]]
   {:db (update db :schedules (fn [schedules]
                                (remove #(= (:id %) (:id schedule)) schedule)))
    :dispatch [:schedule/set-deleting (:id schedule) false]}))

(reg-event-db
 :schedule/set-deleting
 (fn [db [_ id visibility]]
   (assoc-in db [:deleting-schedules id] visibility)))

(reg-event-db
 :schedule/set-new
 (fn [db [_ attr val]]
   (assoc-in db [:new-schedule attr] val)))

(reg-event-fx
 :schedule/create
 (fn [{:keys [db]} [_ dow]]
   (let [new-schedule (:new-schedule db)]
     {:db         (assoc db :new-schedule {:description ""
                                           :time ""
                                           :day_of_the_week ""})
      :http-xhrio {:method :post
                   :uri "/api/schedule"
                   :params new-schedule
                   :format (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords true})
                   :on-success [:schedule/created new-schedule]
                   :on-failure [:schedule/error-on-create]}})))

(reg-event-db
 :schedule/error-on-create
 (fn [db _]
   (.alert js/window "Oops, não foi possível adicionar a programação")
   db))

(reg-event-db
 :schedule/created
 (fn [db [_ new-schedule result]]
   (update db :schedules conj (assoc new-schedule :id (result "schedule-id")))))
