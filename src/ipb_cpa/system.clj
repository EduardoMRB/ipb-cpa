(ns ipb-cpa.system
  (:require [com.stuartsierra.component :as component]
            [ipb-cpa.mail :as mail]))

;; Components
(defrecord Database [connection-uri]
  component/Lifecycle
  (start [component]
    (assoc component :db {:connection-uri connection-uri}))
  (stop [component]
    (dissoc component :db)))

(defn make-database [connection-uri]
  (->Database connection-uri))

(defrecord Mailer [host port user pass]
  mail/IMailer
  (send-mail [this to from message]
    (mail/postal-send-mail this to from message)))

(defn make-mailer [{:keys [host port user pass]}]
  (->Mailer host port user pass))

;; System
(defn system [connection-uri mailer-params]
  (component/system-map
   :database (make-database connection-uri)
   :mailer (make-mailer mailer-params)))
