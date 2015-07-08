(ns ipb-cpa.helper
  (:require [om.core :as om]))

(defn get-input-value [input]
  (.-value input))

(defn get-target-value [e]
  (get-input-value (.-target e)))

(defn update-owner-state! [owner k e]
  (om/set-state! owner k (get-target-value e)))
