(ns ipb-cpa.views
  (:require [ipb-cpa.components.schedule :as schedule]
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
        (case @active-panel
          :home-panel [dashboard]
          :schedule-panel [schedule/schedule-panel]
          :videos-panel [videos]
          [:h1 "whoops"])]

       [:footer.admin-footer]])))
