(ns ipb-cpa.view.about
  (:require [ipb-cpa.view.layout :as layout]
            [hiccup.core :as hiccup]))

(defn card [title icon]
  [:li
   [:div.card
    [:div.image-wrapper
     [:i.fa.image {:class (str "fa-" icon)}]]
    [:div.title-wrapper
     [:span title]]]])

(defn about []
  (layout/default-template
    (list
     [:div.columns
      [:h2 "Sobre"]
      [:ul.small-block-grid-2.medium-block-grid-3.large-block-grid-4
       (card "Símbolos de fé" "glass")
       (card "História" "history")
       (card "Ministro" "male")
       (card "Conselho" "users")
       (card "Junta Diaconal" "heart")]])))

