(ns ipb-cpa.site.view
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]
            [ipb-cpa.site.daily-verse :refer [get-verse]]))

(defn random-verses []
  [:div.verse-container
   [:p (:text (get-verse))]
   [:em (:reference (get-verse))]])

(defn menu []
  [:nav.top-nav
   [:ul.top-menu
    [:li [:a {:href "#"} "Sobre"]]
    [:li [:a {:href "#"} "Programação"]]
    [:li [:a {:href "#"} "Mensagens e Estudos"]]
    [:li [:a {:href (url-for :site#contact)} "Fale Conosco"]]
    [:li [:a {:href "#"} "Missões"]]]])

(defn header []
  [:div.row
   [:a {:href (url-for :site#index)}
    [:img {:src "images/ipb-logo.png"}]]
   (menu)])

(defn layout [body]
  (html5 {:lang "pt-br"}
         [:head
          [:title "Igreja Presbiteriana do CPA IV"]
          [:meta {:name :view-port
                  :context "width=device-width, initial-scale=1"}]
          [:link {:href "css/app.css"
                  :rel "stylesheet"}]
          [:body
           [:div.container
            ;; header.
            (header)
            ;; Site content.
            [:main.row.site-content
             body]
            ;; Site footer.
            [:footer.site-footer
             [:h4 "Como chegar"]
             ;; Google Maps iframe.
             [:iframe {:src "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d480.43820863243894!2d-56.036553589325415!3d-15.564608827688991!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0000000000000000%3A0x08cb3ad878331829!2s1%C2%B0+Igreja+Presbiteriana+do+CPA+4!5e0!3m2!1spt-BR!2sbr!4v1433469171863"
                       :width "1140"
                       :height "200"
                       :frameborder "0"
                       :style "border:0"}]]
           ;; JavaScript resources.
           [:script {:src "http://code.jquery.com/jquery-2.1.0.min.js"}]
           [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"}]
           [:script {:src "js/out/goog/base.js"}]
           [:script {:src "js/app.js"}]
           [:script "goog.require('ipb_cpa.core');"]]]]))

(defn index []
  (layout (random-verses)))

(defn contact []
  (layout
    (list
      [:h1 "Fale com o pastor"]
      [:div.row
       [:form {:method "POST"
               :action ""}
        [:div.form-group
         [:label {:for "name"} "Nome"]
         [:input#name.form-control {:type "text" :placeholder "Digite seu nome"}]]
        [:div.form-group
         [:label {:for "email"} "Email"]
         [:input#email.form-control {:type "text" :placeholder "Digite seu email"}]]
        [:div.form-group
         [:label {:for "message"} "Mensagem"]
         [:textarea#email.form-control {:placeholder "Digite sua mensagem"}]]
        [:button.btn.btn-default "Enviar"]]])))
