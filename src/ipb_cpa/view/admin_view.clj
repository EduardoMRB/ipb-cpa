(ns ipb-cpa.view.admin-view
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]
            [cheshire.core :as json]
            [ipb-cpa.db :as db]))

(def sections
  [{:handler-name :admin.schedule#index :name "Programação"}
   {:handler-name :admin.video#index    :name "Vídeos"}])

(defn menu []
  [:ul
   (for [section sections]
     [:li
      [:a {:href (url-for (:handler-name section))} (:name section)]])])

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
             [:div
              ;; Here the app will be mounted.
              body

              ;; JavaScript resources.
              [:script {:src "/js/compiled/app.js"}]
              [:script
               "ipb_cpa.core.init()"]
              scripts]]])))

(defn schedule-index [db]
  (layout {:body [:div#schedule-component.row]
           :scripts [:script "goog.require('ipb_cpa.admin_schedule');"]}))

(defn video-index [db]
  (layout {:body [:div#video-component]
           :scripts [:script "goog.require('ipb_cpa.admin_video');"]}))

(defn login-page []
  (layout {:body [:div.columns
                  [:div.small-6.small-centered.large-4.columns
                  [:h4 "Primeira Igreja Presbiteriana do CPA IV"]
                   [:form
                    [:div.small-12.columns
                      [:label "Login"
                       [:input {:type "text"}]]]
                    [:div.small-12.columns
                     [:label "Senha"
                      [:input {:type "password"}]]]
                    [:button.expand.round "Entrar"]]]]}))

(defn dashboard []
  (layout {:body [:div#app]}))
