(ns ipb-cpa.site.view
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]
            [ipb-cpa.site.daily-verse :refer [get-verse]]))

(defn menu []
  [:nav.top-nav.large-10.columns
   [:ul.top-menu
    [:li [:a {:href "#"} "Sobre"]]
    [:li [:a {:href "#"} "Programação"]]
    [:li [:a {:href "#"} "Mensagens e Estudos"]]
    [:li [:a {:href (url-for :site#contact)} "Fale Conosco"]]
    [:li [:a {:href "#"} "Missões"]]]])

(defn header []
  [:div.row.site-header
   [:a.large-2.columns {:href (url-for :site#index)}
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
           [:div.row
            ;; header.
            (header)
            ;; Site content.
            [:main.row.site-content
             body]
            ;; Site footer.
            [:footer.row.site-footer
             [:h2 "Como chegar"]
             ;; Google Maps iframe.
             [:iframe {:src "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d480.43820863243894!2d-56.036553589325415!3d-15.564608827688991!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0000000000000000%3A0x08cb3ad878331829!2s1%C2%B0+Igreja+Presbiteriana+do+CPA+4!5e0!3m2!1spt-BR!2sbr!4v1433469171863"
                       :width "1140"
                       :height "200"
                       :frameborder "0"
                       :style "border:0"}]]
           ;; JavaScript resources.
           [:script {:src "js/out/goog/base.js"}]
           [:script {:src "js/app.js"}]
           [:script "goog.require('ipb_cpa.core');"]]]]))

(defn random-verses []
  [:div.small-12.large-6.columns
   [:h2 "Versículo do dia"]
   [:p (:text (get-verse))]
   [:em (:reference (get-verse))]])

(defn schedule-list [title events]
  (list
    [:h3 title]
    [:ul
     (for [[time desc] events]
       [:li [:em time] (str " - " desc)])]))

(defn weekly-schedule []
  [:div.small-12.large-6.columns
   [:h2 "Programação Semanal"]
   (schedule-list "Terça" [["19:30" "Reunião nos lares"]])
   (schedule-list "Quinta" [["19:30" "Reunião de oração"]])
   (schedule-list "Domingo" [["08:30" "Escola bíblica dominical"]
                             ["19:00" "Culto da família"]])])

(defn home-first-row []
  [:div.row
   (weekly-schedule)
   (random-verses)])

(defn index []
  (layout (home-first-row)))

(defn contact []
  (layout
    (list
      [:div.row
       [:div.small-6.large-5.columns
        [:h1 "Fale com o pastor"]
        [:form {:method "POST"
                :action ""}
         [:div
          [:label {:for "name"} "Nome"]
          [:input#name {:type "text" :placeholder "Digite seu nome"}]]
         [:div
          [:label {:for "email"} "Email"]
          [:input#email {:type "text" :placeholder "Digite seu email"}]]
         [:div
          [:label {:for "message"} "Mensagem"]
          [:textarea#email {:placeholder "Digite sua mensagem"}]]
         [:button.button "Enviar"]]]
       [:div.small-6.large-offset-3.large-4.columns
        [:h2 "Ipsum natus minus"]
        [:p "Amet hic laborum corrupti laboriosam est quas, maxime pariatur! Illo adipisci repellat earum recusandae iste distinctio soluta sequi facilis, excepturi officia! Ad cupiditate perferendis sint dolore quisquam tempora accusantium, explicabo?"]]])))
