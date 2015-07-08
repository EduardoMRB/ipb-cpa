(ns ipb-cpa.admin-video
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :as html :refer-macros [html]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [cljs.core.async :as async :refer [chan put! <! >! alts!]]
            [ipb-cpa.helper :as helper]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(enable-console-print!)

(def app-state (atom {}))

;; =============================================================================
;; Validation
;; =============================================================================

(comment
 {:title "Literatura e Cristianismo"
  :date "2015-11-11"
  :excerpt "Literatura e Cristianiso soa assuntos nao tao distantes como a
           maioria gosta de pensar, nesse video veremos um pouco sobre a
           historia do cristianismo na literatura"
  :embedded-iframe "<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/5jpRRcoeQ2Y\" frameborder=\"0\" allowfullscreen></iframe>"
  :active? true})

(defn embedded-valid? [embedded]
  (re-matches #"<iframe([^>]+)></iframe>" embedded))

(defn validate-video [video]
  (b/validate video
              :title           [[v/required :message "O título é obrigatório"]]
              :date            [[v/required :message "A data é obrigatória"]
                                [v/matches #"\d{4}-\d{2}-\d{2}"
                                 :message "O formato da data é inválido"]]
              :excerpt         [[v/required :message "Escreva um resumo"]]
              :embedded-iframe [[v/required
                                 :message "Precisamos do codigo do video"]
                                [v/matches #"<iframe([^>]+)></iframe>"
                                 :message "Codigo de video invalido"]]
              :active?         [[v/boolean :message "Valor incorreto"]]))

;; =============================================================================
;; Component functions
;; =============================================================================

(defn set-embedded-iframe [owner e]
  (let [embedded (-> e .-target .-value)
        error    (if (embedded-valid? embedded)
                   ""
                   "Código de incorporação inválido")]
    (om/set-state! owner :embedded-iframe embedded)
    (om/set-state! owner :embedded-error error)))

;; =============================================================================
;; Components
;; =============================================================================

(defn new-video [data owner]
  (reify
   om/IInitState
   (init-state [_]
     {:embedded-iframe ""
      :embedded-error ""
      :title ""
      :date ""
      :excerpt ""
      :active? true
      :errors {}})
   om/IRenderState
   (render-state [_ {:keys [embedded-iframe embedded-error title date excerpt
                            active? errors]}]
     (html
       [:form
        [:fieldset
         [:legend "Adicionar video"]
         [:div.row
          [:div.small-6.columns
           [:label "Título"
            [:input {:type "text"
                     :value title
                     :on-change #(helper/update-owner-state! owner :title %)}]]
           (helper/error-message-for errors :title)]
          [:div.small-6.columns
           [:label "Data"
            [:input {:type "text"
                     :value date
                     :on-change #(helper/update-owner-state! owner :date %)}]]
           (helper/error-message-for errors :date)]]
         [:div.row
          [:div.small-12.columns
           [:label "Resumo"
            [:textarea {:on-change #(helper/update-owner-state! owner :excerpt %)
                        :rows 10
                        :value excerpt}]]
           (helper/error-message-for errors :excerpt)]]
         [:div.row.collapse
          [:div.small-6.columns
           [:div.small-12.columns
            [:label "Incorporar"
             [:input {:type "text"
                      :value embedded-iframe
                      :on-change #(set-embedded-iframe owner %)}]]
            (helper/error-message-for errors :embedded-iframe)]
           [:div.small-12.columns
            [:label "Ativo?"]
            [:div.switch
             [:input#active {:type "checkbox"
                             :checked active?
                             :on-change #(om/set-state! owner
                                                        :active?
                                                        (not active?))}]
             [:label {:for "active"}]]
            (helper/error-message-for errors :active?)]]
          [:div.small-6.columns
           [:label "Preview do video"]
           [:div.flex-video.widescreen.youtube
            {:dangerouslySetInnerHTML {:__html (if (seq embedded-error)
                                                 embedded-error
                                                 embedded-iframe)}}]]]
         [:div.row
          [:button.small.right {:type "button"
                    :on-click (fn [e]
                                (let [[errors video] (validate-video
                                                      {:title title
                                                       :date date
                                                       :excerpt excerpt
                                                       :embedded-iframe embedded-iframe
                                                       :active? active?})]
                                  (if (seq errors)
                                    (om/set-state! owner :errors errors))))}
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

;; =============================================================================
;; Om root
;; =============================================================================

(om/root video
         app-state
         {:target (.getElementById js/document "video-component")})
