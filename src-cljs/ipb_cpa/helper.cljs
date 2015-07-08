(ns ipb-cpa.helper
  (:require [om.core :as om]
            [sablono.core :as html :refer-macros [html]]))

(defn get-input-value [input]
  (.-value input))

(defn get-target-value [e]
  (get-input-value (.-target e)))

(defn update-owner-state! [owner k e]
  (om/set-state! owner k (get-target-value e)))

(defn error-message-for
  "Takes a map of errors and a key, checks if there is errors associated with
  that key and if it is, returns a React html fragment for the error."
  [errors k]
  (if-let [[error] (errors k)]
    (html
      [:small.error error])))
