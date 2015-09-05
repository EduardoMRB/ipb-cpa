(ns ipb-cpa.site.about
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [put! >! <! chan]]
            [goog.dom :as dom]
            [goog.events :as events]
            [domina :as domina]))

(enable-console-print!)

(defn events [el type]
  (let [out (chan)]
    (events/listen el type (fn [e] (put! out e)))
    out))

(defn ^:export init []
  (let [cards      (dom/getElementsByClass "card")
        chans      (map events cards (repeat "click"))
        event-chan (async/merge chans)]
    (go
      (while true
        (let [e   (<! event-chan)
              url (-> e .-currentTarget (.getAttribute "data-url"))]
          (set! (.-location js/window) url))))))
