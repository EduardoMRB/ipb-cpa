(ns ipb-cpa.view.admin-view
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]))

(defn layout [body]
  (html5 {:lang "pt-br"}
         [:head
          [:title "Igreja Presbiteriana do CPA IV"]
          [:meta {:name :view-port
                  :context "width=device-width, initial-scale=1"}]
          [:link {:href "css/app.css"
                  :rel "stylesheet"}]
          [:body
           [:div.row
            ;; Content.
            [:main.row.admin-content
             body]
            ;; Footer.
            [:footer.row.admin-footer]
           ;; JavaScript resources.
           [:script {:src "js/out/goog/base.js"}]
           [:script {:src "js/app.js"}]
           [:script "goog.require('ipb_cpa.core');"]]]]))

(defn schedule-index []
  (layout [:h1 "Programacao - Admin"]))
