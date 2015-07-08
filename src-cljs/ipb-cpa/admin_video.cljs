(ns ipb-cpa.admin-video
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :as html :refer-macros [html]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [cljs.core.async :as async :refer [chan put! <! >! alts!]]))

(enable-console-print!)

(def app-state (atom {}))

(defn new-video [data owner]
  (reify
   om/IRender
   (render [_]
     (html
       [:form
        [:fieldset
         [:legend "Adicionar video"]
         [:div.row
          [:div.small-6.columns
           [:label "Título"
            [:input {:type "text"
                     :on-change #(.log js/console "changed title")
                     :ref "title"}]]]
          [:div.small-6.columns
           [:label "Data"
            [:input {:type "text"
                     :on-change #(.log js/console "changed date")
                     :ref "date"}]]]]
         [:div.row
          [:div.small-12.columns
           [:label "Resumo"
            [:textarea {:on-change #(.log js/console "changed excerpt")
                        :ref "exerpt"
                        :rows 10}]]]]
         [:div.row.collapse
          [:div.small-6.columns
           [:div.small-12.columns
            [:label "Incorporar"
             [:input {:type "text"
                      :on-change #(.log js/console "embedded changed")
                      :ref "embedded"}]]]
           [:div.small-12.columns
            [:label "Ativo?"]
            [:div.switch
             [:input#active {:type "checkbox"
                             :on-change #(.log js/console "active changed")
                             :ref "active"}]
             [:label {:for "active"}]]]]
          [:div.small-6.columns
           [:div.flex-video]]]
         [:div.row
          [:button.small.right {:type "button"
                    :on-click #(.log js/console "new-button clicked")
                    :ref "new-button"}
           "Criar"]]]]))))

(defn video [data owner]
  (reify
   om/IRender
   (render [_]
     (html
       [:div
        [:h2 "Video component"]
        [:div
         (om/build new-video data)]]))))

(om/root video
         app-state
         {:target (.getElementById js/document "video-component")})
