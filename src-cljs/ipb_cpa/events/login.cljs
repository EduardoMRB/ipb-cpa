(ns ipb-cpa.events.login
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]))

(def local-storage (.-localStorage js/window))

(defn storage-set [k v]
  (.setItem local-storage k v))

(reg-event-db
 :login/set-attr
 (fn [db [_ keys value]]
   (assoc-in db (apply vector :login/data keys) value)))

(reg-event-fx
 :login/submit
 (fn [{:keys [db]} _]
   {:db         db
    :http-xhrio {:method :put
                 :uri "/api/token"
                 :params (:login/data db)
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:login/set-token]
                 :on-failure [:login/set-error]}}))

(reg-event-fx
 :login/set-token
 (fn [{:keys [db]} [_ resp]]
   {:db       (-> db
                  (assoc :token (:token resp))
                  (assoc-in [:login/errors :general] []))
    :dispatch [:login/save-token (:token resp)]}))

(reg-event-db
 :login/set-error
 (fn [db [_ err]]
   (assoc-in db [:login/errors :general] ["Email ou senha inválidos."])))

(reg-event-db
 :login/save-token
 (fn [db [_ token]]
   (storage-set "token" token)
   db))
