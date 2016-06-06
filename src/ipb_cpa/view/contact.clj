(ns ipb-cpa.view.contact
  (:require [io.pedestal.http.route :refer [url-for]]
            [ipb-cpa.view.layout :as layout]))

(defn contact [flash]
  (layout/default-template
    (list
     [:div.row
      (if flash
        [:div.small-12.columns.alert-box.success.radius flash])
      [:div.small-6.large-5.columns
       [:h1 "Fale com o pastor"]
       [:form {:method "POST"
               :action (url-for :site#send-message)}
        [:div
         [:label {:for "name"} "Nome"]
         [:input#name {:type "text" :placeholder "Digite seu nome" :name "name"}]]
        [:div
         [:label {:for "email"} "Email"]
         [:input#email {:type "text" :placeholder "Digite seu email" :name "email"}]]
        [:div
         [:label {:for "message"} "Mensagem"]
         [:textarea#message {:placeholder "Digite sua mensagem" :name "message" :rows 20}]]
        [:button.button "Enviar"]]]
      [:div.small-6.large-offset-3.large-4.columns
       [:img.th {:src "/images/pastor-manoel.jpg"}]
       [:h4 "Rev. Manoel Seixas"]
       [:p "Bacharel em Engenharia Elétrica pela UFMT, formado em Teologia e Bacharel em Direito pela UFMT."]]])
    (layout/footer-map)))
