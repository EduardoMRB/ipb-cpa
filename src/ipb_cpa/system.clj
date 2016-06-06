(ns ipb-cpa.system
  (:require [com.stuartsierra.component :as component]))

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
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this))

(defn make-mailer [{:keys [host port user pass]}]
  (->Mailer host port user pass))
(make-mailer {:host "localhost" :port 1025 :user "" :pass ""})

;; System
(defn system [connection-uri mailer-params]
  (component/system-map
   :database (make-database connection-uri)
   :mailer (make-mailer mailer-params)))
