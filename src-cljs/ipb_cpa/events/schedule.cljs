(ns ipb-cpa.events.schedule
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [com.rpl.specter :as specter :refer [setval ALL]]))

;; ------------------------------
;; Validation
;; ------------------------------

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

(reg-fx
 :schedule/validate
 (fn validate [{:keys [schedule on-validate on-error]}]
   (let [[errors schedule] (validate-schedule schedule)]
     (if errors
       (rf/dispatch (conj on-error schedule errors))
       (rf/dispatch (conj on-validate schedule))))))

(reg-event-fx
 :schedule/update
 (fn [{:keys [db]} [_ schedule]]
   {:db                db
    :schedule/validate {:schedule    schedule
                        :on-validate [:schedule/put-schedule]
                        :on-error    [:schedule/validation-error]}}))

(reg-event-db
 :schedule/validation-error
 (fn [db [_ schedule errors]]
   (assoc-in db [:editing-schedules-errors (:id schedule)] errors)))

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
                 :uri (str "/api/schedule/" (:id schedule) "?token=" (:token db))
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
                (assoc-in db [:editing-schedules-errors (:id schedule)] nil))
    :dispatch [:schedule/set-editing (:id schedule) false]}))

(reg-event-fx
 :schedule/cancel-editing
 (fn [{:keys [db]} [_ schedule]]
   (let [schedule-id (:id schedule)]
     {:db (assoc-in db [:editing-schedules-errors schedule-id] nil)
      :dispatch [:schedule/set-editing schedule-id false]})))

(reg-event-db
 :schedule/set-editing
 (fn [db [_ id visibility]]
   (assoc-in db [:editing-schedules id] visibility)))

(reg-event-fx
 :schedule/delete-schedule
 (fn [{:keys [db]} [_ schedule]]
   {:db db
    :http-xhrio {:method :delete
                 :uri (str "/api/schedule/" (:id schedule) "?token=" (:token db))
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
   {:db (update db
                :schedules
                (fn [schedules]
                  (remove #(= (:id %) (:id schedule)) schedules)))
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
 (fn [{:keys [db]} [_ dow on-success]]
   {:db db
    :schedule/validate {:schedule (assoc (:new-schedule db) :day_of_the_week dow)
                        :on-validate [:schedule/post on-success]
                        :on-error [:schedule/creation-errors]}}))

(reg-event-db
 :schedule/creation-errors
 (fn [db [_ _schedule errors]]
   (assoc-in db [:new-schedule :errors] errors)))

(reg-event-fx
 :schedule/post
 (fn [{:keys [db]} [_ on-success new-schedule]]
   {:db         (assoc db :new-schedule {:description     ""
                                         :time            ""
                                         :day_of_the_week ""
                                         :errors          nil})
    :http-xhrio {:method          :post
                 :uri             (str "/api/schedule?token=" (:token db))
                 :params          new-schedule
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords true})
                 :on-success      [:schedule/created on-success new-schedule]
                 :on-failure      [:schedule/error-on-create]}}))

(reg-event-db
 :schedule/error-on-create
 (fn [db _]
   (.alert js/window "Oops, não foi possível adicionar a programação")
   db))

(reg-event-db
 :schedule/created
 (fn [db [_ on-success new-schedule result]]
   (on-success)
   (update db :schedules conj (assoc new-schedule :id (result "schedule-id")))))

(reg-event-fx
 :schedule/load-schedule
 (fn [{:keys [db]} _]
   {:db db
    :http-xhrio {:method :get
                 :uri (str "/api/schedule?token=" (:token db))
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:set-schedules]
                 :on-failure [:schedule/load-error]}}))

(reg-event-db
 :schedule/load-error
 (fn [db [_ err]]
   (println err)
   db))
