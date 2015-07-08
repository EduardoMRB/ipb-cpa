(ns ipb-cpa.admin-video
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :as html :refer-macros [html]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [cljs.core.async :as async :refer [chan put! <! >! alts!]]
            [ipb-cpa.helper :as helper]))

(enable-console-print!)

(def app-state (atom {}))

(defn embedded-valid? [embedded]
  (re-matches #"<iframe([^>]+)></iframe>" embedded))

(defn set-embedded-iframe [owner e]
  (let [embedded (-> e .-target .-value)
        error    (if (embedded-valid? embedded)
                   ""
                   "Código de incorporação inválido")]
    (om/set-state! owner :embedded-iframe embedded)
    (om/set-state! owner :embedded-error error)))

(defn new-video [data owner]
  (reify
   om/IInitState
   (init-state [_]
     {:embedded-iframe ""
      :embedded-error ""
      :title ""
      :date ""
      :excerpt ""
      :active? true})
   om/IRenderState
   (render-state [_ {:keys [embedded-iframe embedded-error title date excerpt active?]}]
     (html
       [:form
        [:fieldset
         [:legend "Adicionar video"]
         [:div.row
          [:div.small-6.columns
           [:label "Título"
            [:input {:type "text"
                     :value title
                     :on-change #(helper/update-owner-state! owner :title %)}]]]
          [:div.small-6.columns
           [:label "Data"
            [:input {:type "text"
                     :value date
                     :on-change #(helper/update-owner-state! owner :date %)}]]]]
         [:div.row
          [:div.small-12.columns
           [:label "Resumo"
            [:textarea {:on-change #(helper/update-owner-state! owner :excerpt %)
                        :rows 10
                        :value excerpt}]]]]
         [:div.row.collapse
          [:div.small-6.columns
           [:div.small-12.columns
            [:label "Incorporar"
             [:input {:type "text"
                      :value embedded-iframe
                      :on-change #(set-embedded-iframe owner %)}]]]
           [:div.small-12.columns
            [:label "Ativo?"]
            [:div.switch
             [:input#active {:type "checkbox"
                             :checked active?
                             :on-change #(om/set-state! owner
                                                        :active?
                                                        (not active?))}]
             [:label {:for "active"}]]]]
          [:div.small-6.columns
           [:label "Preview do video"]
           [:div.flex-video
            {:dangerouslySetInnerHTML {:__html (if (seq embedded-error)
                                                 embedded-error
                                                 embedded-iframe)}}]]]
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
