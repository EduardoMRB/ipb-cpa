(ns ipb-cpa.view.admin-view
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]
            [cheshire.core :as json]
            [ipb-cpa.db :as db]))

(defn layout [args]
  (let [{:keys [body scripts] :or [body nil scripts nil]} args]
    (html5 {:lang "pt-br"}
           [:head
            [:title "Igreja Presbiteriana do CPA IV"]
            [:meta {:name :view-port
                    :context "width=device-width, initial-scale=1"}]
            [:link {:href "/css/app.css"
                    :rel "stylesheet"}]
            [:body
             [:div.row
              ;; Content.
              [:main.row.admin-content
               body]
              ;; Footer.
              [:footer.row.admin-footer]
              ;; JavaScript resources.
              [:script {:src "/js/out/goog/base.js"}]
              [:script {:src "/js/app.js"}]
              [:script "goog.require('ipb_cpa.core');"]
              scripts]]])))

(defn schedule-index [db]
  (layout {:body (list [:h1 "Programacao - Admin"]
                       [:div#schedule-component])
           :scripts [:script "goog.require('ipb_cpa.admin_schedule');"]}))

(defn json-schedules [database]
  (json/generate-string (db/get-schedules database)))
