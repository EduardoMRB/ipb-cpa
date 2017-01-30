(ns ipb-cpa.views
  (:require [ipb-cpa.components.schedule :as schedule]
            [ipb-cpa.components.videos :as videos]
            [re-frame.core :as rf :refer [subscribe]]))

(def sections
  [{:url "#/schedule" :name "Programação"}
   {:url "#/videos" :name "Vídeos"}])

(defn menu []
  [:ul
   (for [section sections]
     ^{:key (:url section)}
     [:li
      [:a {:href (:url section)} (:name section)]])])

(defn dashboard []
  [:div.columns
   [:h1 "Here is the dashboard"]])

(defn videos []
  [:div.columns
   [:h1 "Here are the videos"]])

(defmulti page identity)

(defmethod page :home-panel [_]
  [dashboard])

(defmethod page :schedule-panel [_]
  [schedule/schedule-panel])

(defmethod page :videos-panel [_]
  [videos/videos-panel])

(defmethod page :default [_]
  [:h1 "whoops"])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])]
    (fn []
      [:div
       [:nav.top-bar
        [:ul.title-area
         [:li.name
          [:h1
           [:a {:href "#/"} "Admin"]]]]
        [:section.top-bar-section
         [menu]]]

       [:main.admin-content
        (page @active-panel)]

       [:footer.admin-footer]])))
