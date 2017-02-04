(ns ipb-cpa.views
  (:require [ipb-cpa.components.login :as login]
            [ipb-cpa.components.schedule :as schedule]
            [ipb-cpa.components.videos :as videos]
            [re-frame.core :as rf :refer [dispatch subscribe]]))

(def sections
  [{:url "#/schedule" :name "Programação" :panel :schedule-panel}
   {:url "#/videos" :name "Vídeos" :panel :videos-panel}])

(defn menu [active-panel]
  [:ul
   (for [section sections]
     ^{:key (:url section)}
     [:li {:class (when (= active-panel (:panel section))
                    "active")}
      [:a {:href (:url section)} (:name section)]])])

(defn dashboard []
  [:div.columns
   [:h1 "Here is the dashboard"]])

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
  (let [active-panel (subscribe [:active-panel])
        logged-in?   (subscribe [:logged-in?])]
    (if @logged-in?
      [:div
       [:nav.top-bar
        [:ul.title-area
         [:li.name
          [:h1
           [:a {:href "#/"} "Admin"]]]]
        [:section.top-bar-section
         [menu @active-panel]]]

       [:main.admin-content
        (page @active-panel)]

       [:footer.admin-footer]]

      [login/login-screen])))
