(ns ipb-cpa.helper)

(defn get-input-value [input]
  (.-value input))

(defn get-target-value [e]
  (get-input-value (.-target e)))

(defn error-message-for
  "Takes a map of errors and a key, checks if there is errors associated with
  that key and if it is, returns a React html fragment for the error."
  [errors k]
  (if-let [[error] (errors k)]
    [:small.error error]))
