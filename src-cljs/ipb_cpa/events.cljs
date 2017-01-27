(ns ipb-cpa.events
  (:require [ipb-cpa.db :as db]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [com.rpl.specter :as specter :refer [setval ALL]]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/app-db))

(reg-event-db
 :set-active-panel
 (fn [db [_ panel]]
   (assoc db :active-panel panel)))

(reg-event-db
 :set-schedules
 (fn [db [_ schedules]]
   (assoc db :schedules schedules)))

(reg-event-db
 :schedule/set-active-tab
 (fn [db [_ day]]
   (let [all-inactive-dow (into (array-map)
                                (map (fn [[k v]]
                                       [k false])
                                     (:days-of-the-week db)))]
     (assoc db :days-of-the-week (assoc all-inactive-dow day true)))))

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
   {:db (setval [ALL #(= (:id schedule) (:id %))]
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
