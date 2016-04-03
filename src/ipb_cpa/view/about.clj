(ns ipb-cpa.view.about
  (:require [ipb-cpa.view.layout :as layout]
            [hiccup.core :as hiccup]
            [io.pedestal.http.route :refer [url-for]]))

(defn card [title icon url]
  [:li
   [:div.card {:data-url url}
    [:div.image-wrapper
     [:i.fa.image {:class (str "fa-" icon)}]]
    [:div.title-wrapper
     [:span title]]]])

(defn about []
  (layout/default-template
    (list
     [:div.columns
      [:ul.small-block-grid-3.medium-block-grid-4.large-block-grid-5
       (card "Símbolos de fé" "glass" (url-for :site.about#faith-symbols))
       (card "História" "history" (url-for :site.about#history))
       (card "Ministro" "male" (url-for :site.about#ministry))
       (card "Conselho" "users" (url-for :site.about#council))
       (card "Junta Diaconal" "heart" (url-for :site.about#deacon-board))]]
     ;; JavaScript resources.
     [:script {:src "/js/out/goog/base.js"}]
     [:script {:src "/js/app.js"}]
     [:script "goog.require('ipb_cpa.site.about');"]
     [:script "ipb_cpa.site.about.init();"])))
