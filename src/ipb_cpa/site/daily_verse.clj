(ns ipb-cpa.site.daily-verse
  (:require [taoensso.carmine :as car :refer [wcar]]))

;; Default conn.
(def ^{:private true} conn nil)

(def ^{:private true} verses (read-string (slurp "resources/bible-verses.edn")))

;; One day in seconds
(def ^{:private true} one-day (* 24 3600))

(defn- random-verse []
  (rand-nth verses))

(defn- set-verse []
  (wcar conn
        (car/set "verse" (random-verse))
        (car/expire "verse" one-day)))

(defn get-verse []
  (if-let [verse (wcar conn (car/get "verse"))]
    verse
    (do
      (set-verse)
      (wcar conn (car/get "verse")))))
