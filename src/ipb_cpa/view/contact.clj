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
       [:h2 "Ipsum natus minus"]
       [:p "Amet hic laborum corrupti laboriosam est quas, maxime pariatur! Illo adipisci repellat earum recusandae iste distinctio soluta sequi facilis, excepturi officia! Ad cupiditate perferendis sint dolore quisquam tempora accusantium, explicabo?"]]])
    (layout/footer-map)))
