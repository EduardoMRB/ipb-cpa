(ns ipb-cpa.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [ajax.core :refer [DELETE GET POST PUT]]
            [re-frame.core :as re-frame]))

;; ------------
;; Schedule stuff, maybe place it somewhere else?

(defn sort-schedules
  "Sort schedules by time in ascending order."
  [schedules]
  (sort (fn [a b]
          (println (:time a))
          (let [fmt    (f/formatters :hour-minute)
                a-time (f/parse fmt (:time a))
                b-time (f/parse fmt (:time b))]
            (cond (t/after? a-time b-time) 1
                  (t/after? b-time a-time) -1
                  :else                    0)))
        schedules))

(defn handler [resp]
  (re-frame/dispatch [:set-schedules resp]))

(defn err-handler [resp]
  (.log js/console "something went wrong" resp))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
    (re-frame/dispatch [:set-active-panel :home-panel]))

  (defroute "/schedule" []
    (GET "/api/schedule"
        {:handler handler
         :error-handler err-handler
         :response-format :json
         :keywords? true})
    (re-frame/dispatch [:set-active-panel :schedule-panel]))

  (defroute "/videos" []
    (re-frame/dispatch [:set-active-panel :videos-panel]))

  ;; --------------------
  (hook-browser-navigation!))
