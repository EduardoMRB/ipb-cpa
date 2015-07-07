(ns ipb-cpa.admin-video
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :as html :refer-macros [html]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [cljs.core.async :as async :refer [chan put! <! >! alts!]]))

(enable-console-print!)

(def app-state (atom {}))

(defn video [data owner]
  (reify
   om/IRender
   (render [_]
     (html
       [:h2 "Video component"]))))

(om/root video
         app-state
         {:target (.getElementById js/document "video-component")})
