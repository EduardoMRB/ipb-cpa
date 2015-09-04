(ns ipb-cpa.view.about
  (:require [ipb-cpa.view.layout :as layout]
            [hiccup.core :as hiccup]))

(defn card [title icon]
  [:li
   [:div.card {:data-url "http://google.com"}
    [:div.image-wrapper
     [:i.fa.image {:class (str "fa-" icon)}]]
    [:div.title-wrapper
     [:span title]]]])

(defn about []
  (layout/default-template
    (list
     [:div.columns
      [:h2 "Sobre"]
      [:ul.small-block-grid-3.medium-block-grid-4.large-block-grid-5
       (card "Símbolos de fé" "glass")
       (card "História" "history")
       (card "Ministro" "male")
       (card "Conselho" "users")
       (card "Junta Diaconal" "heart")]]
     ;; JavaScript resources.
     [:script {:src "/js/out/goog/base.js"}]
     [:script {:src "/js/app.js"}]
     [:script "goog.require('ipb_cpa.site.about');"])))
