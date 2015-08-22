(ns ipb-cpa.view.layout
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]))

(defn menu []
  [:nav.top-nav.large-10.small-12.columns
   [:ul.top-menu
    [:li [:a {:href "#"} "Sobre"]]
    [:li [:a {:href "#"} "Programação"]]
    [:li [:a {:href "#"} "Mensagens e Estudos"]]
    [:li [:a {:href "#"} "Missões"]]
    [:li [:a {:href (url-for :site#contact)} "Fale Conosco"]]]])

(defn header []
  [:div.row.site-header
   [:a.large-2.large-offset-0.small-offset-3.small-6.columns {:href (url-for :site#index)}
    [:img {:src "images/ipb-logo-without-text.png"}]]
   (menu)])

(defn default-template [body]
  (html5 {:lang "pt-br"}
         [:head
          [:title "Igreja Presbiteriana do CPA IV"]
          [:meta {:name :view-port
                  :context "width=device-width, initial-scale=1"}]
          [:link {:href "css/app.css"
                  :rel "stylesheet"}]
          [:link {:rel "shortcut icon" :href "favicon.ico"}]]
          [:body
           [:div.row
            ;; header.
            (header)
            ;; Site content.
            [:main.site-content
             body]
            ;; Site footer.
            [:footer.site-footer
             ;; Google Maps iframe.
             [:div.small-12.columns
              [:h2 "Como chegar"]]
             [:div.small-12.columns.google-maps
              [:iframe {:src "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d480.43820863243894!2d-56.036553589325415!3d-15.564608827688991!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0000000000000000%3A0x08cb3ad878331829!2s1%C2%B0+Igreja+Presbiteriana+do+CPA+4!5e0!3m2!1spt-BR!2sbr!4v1433469171863"
                        :width "1140"
                        :height "200"
                        :frameborder "0"
                        :style "border:0"}]]]
           ;; JavaScript resources.
           [:script {:src "js/out/goog/base.js"}]
           [:script {:src "js/app.js"}]
           [:script "goog.require('ipb_cpa.core');"]]]))
