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

;; System
(defn system [connection-uri]
  (component/system-map
   :database (make-database connection-uri)))
