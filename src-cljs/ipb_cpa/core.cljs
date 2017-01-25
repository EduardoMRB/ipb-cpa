(ns ipb-cpa.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            [ipb-cpa.events]
            [ipb-cpa.subs]
            [ipb-cpa.config :as config]
            [ipb-cpa.views :as views]
            [ipb-cpa.routes :as routes]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
