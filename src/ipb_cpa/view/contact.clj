(ns ipb-cpa.view.contact
  (:require [ipb-cpa.view.layout :as layout]))

(defn contact []
  (layout/default-template
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
       [:img.th {:src "/images/pastor-manoel.jpg"}]
       [:h4 "Rev. Manoel Seixas"]
       [:p "Bacharel em Engenharia Elétrica pela UFMT, formado em Teologia e Bacharel em Direito pela UFMT."]]])
    (layout/footer-map)))
