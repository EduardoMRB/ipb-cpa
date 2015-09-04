(ns ipb-cpa.site.about
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [put! >! <! chan]]
            [domina.css :as css]
            [domina.events :as event]))

(enable-console-print!)

(defn events [element ev]
  (let [out (chan)]
    (event/listen! element ev (fn [e] (put! out e)))
    out))

(defn ^:export init []
  (let [cards      (css/sel ".card")
        _ (print cards)
        chans      (map events cards (repeat :click))
        event-chan (async/into (chan) chans)]
    (go
      (while true
        (let [e   (<! event-chan)
              url (-> e .-target (.getAttribute "data-url"))]
          (println e)
          (set! (.-location js/window) url))))))

(init)
