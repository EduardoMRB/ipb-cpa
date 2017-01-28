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

(defn on-enter [f]
  (fn [evt]
    (when (= "Enter" (.-key evt))
      (f evt))))

(def day-kw->day
  {:seg "Segunda" :ter "Terça" :quar "Quarta" :quin "Quinta"
   :sex "Sexta" :sab "Sábado" :dom "Domingo"})

(defn tab-name [tab-keyword]
  (day-kw->day tab-keyword))

(defn active-tab [days-of-week]
  (->> days-of-week
       (filter (fn [[_ active?]]
                 active?))
       (ffirst)
       day-kw->day))
