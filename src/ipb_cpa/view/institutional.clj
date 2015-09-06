(ns ipb-cpa.view.institutional
  (:require [io.pedestal.http.route :as route :refer [url-for]]
            [ipb-cpa.view.layout :as layout]))

(defn institutional-view [title body]
  (layout/default-template
    (list
     [:div.columns
      [:ul.breadcrumbs
       [:li
        [:a {:href (url-for :site#index)} "Home"]]
       [:li
        [:a {:href (url-for :site#about)} "Sobre"]]
       [:li.current title]]
      [:article.panel
       [:h1 title]
       body]])))

(defn faith-symbols-view []
  (institutional-view
   "Simbolos de fé"
   (list
    [:h2 "Lorem Ipsum"]
    [:p "Dollor Ammet"]
    [:h3 "Lorem"]
    [:p "Hiiineinshth"]
    [:p "Dollor ammnet ipsum"])))

(defn history-view []
  (institutional-view
   "História"
   (list
    [:h2 "Lorem Ipsum"]
    [:p "Dollor Ammet"]
    [:h3 "Lorem"]
    [:p "Hiiineinshth"]
    [:p "Dollor ammnet ipsum"])))

(defn ministry-view []
  (institutional-view
   "Ministro"
   (list
    [:h2 "Lorem Ipsum"]
    [:p "Dollor Ammet"]
    [:h3 "Lorem"]
    [:p "Hiiineinshth"]
    [:p "Dollor ammnet ipsum"])))

(defn deacon-board-view []
  (institutional-view
   "Junta Diaconal"
   (list
    [:h2 "Lorem Ipsum"]
    [:p "Dollor Ammet"]
    [:h3 "Lorem"]
    [:p "Hiiineinshth"]
    [:p "Dollor ammnet ipsum"])))

(defn council-view []
  (institutional-view
   "Conselho"
   (list
    [:h2 "Lorem Ipsum"]
    [:p "Dollor Ammet"]
    [:h3 "Lorem"]
    [:p "Hiiineinshth"]
    [:p "Dollor ammnet ipsum"])))
