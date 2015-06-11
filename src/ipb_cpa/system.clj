(ns ipb-cpa.system
  (:require [com.stuartsierra.component :as component]))

;; Components
(defrecord Database [subprotocol subname username password]
  component/Lifecycle
  (start [component]
    (let [db-spec {:subprotocol subprotocol
                   :subname subname
                   :user username
                   :password password}]
      (assoc component :database db-spec)))
  (stop [component]
    (dissoc component :database)))

(defn make-database [config]
  (map->Database {:subprotocol (:subprotocol config)
                  :subname (:subname config)
                  :username (:username config)
                  :password (:password config)}))

;; System
(defn system [config]
  (component/system-map
   :database (make-database config)))
