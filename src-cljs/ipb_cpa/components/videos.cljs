(ns ipb-cpa.components.videos
  (:require [ipb-cpa.helper :as helper]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [reagent.core :as r]
            [cljsjs.react-datepicker]))

(def date-picker (r/adapt-react-class js/DatePicker))

(def ESC-CODE 27)

(defn br-date
  "Takes a `moment` date and returns a string formatted in pt-br."
  [date]
  (.format date "DD/MM/YYYY"))

(defn new-video [{:keys [title date excerpt embedded active? errors]}]
  [:form
   [:fieldset
    [:legend "Adicionar video"]
    [:div.row
     [:div.small-6.columns
      [:label "Título"
       [:input {:type "text"
                :value title
                :on-change #(dispatch [:videos/set-new :title (helper/get-target-value %)])}]]
      (helper/errors-for errors :title)]
     [:div.small-6.columns
      [:label "Data"]
      [date-picker {:selected date
                    :locale "pt-br"
                    :date-format "DD/MM/YYYY"
                    :on-change #(dispatch [:videos/set-new :date %])}]
      (helper/errors-for errors :date)]]
    [:div.row
     [:div.small-12.columns
      [:label "Resumo"
       [:textarea {:on-change #(dispatch [:videos/set-new :excerpt (helper/get-target-value %)])
                   :rows 10
                   :value excerpt}]]
      (helper/errors-for errors :excerpt)]]
    [:div.row.collapse
     [:div.small-6.columns
      [:div.small-12.columns
       [:label "Incorporar"
        [:input {:type "text"
                 :value embedded
                 :on-change #(dispatch [:videos/set-new :embedded (helper/get-target-value %)])}]]
       (helper/errors-for errors :embedded)]
      [:div.small-12.columns
       [:label "Ativo?"]
       [:div.switch
        [:input#active {:type "checkbox"
                        :checked active?
                        :on-change #(dispatch [:videos/swap-new :active not])}]
        [:label {:for "active"}]]
       (helper/errors-for errors :active?)]]
     [:div.small-6.columns
      [:label "Preview do video"]
      [:div.flex-video.widescreen.youtube
       {:dangerouslySetInnerHTML {:__html embedded}}]]]
    [:div.columns
     [:button.small.right {:type "button"
                           :on-click #(dispatch [:videos/create])}
      "Criar"]]]])

(defn dispatch-on-key [key-code dispatch-vector]
  (fn [e]
    (if (= (.-keyCode e) key-code)
      (dispatch dispatch-vector))))

(defn expanded-row [video]
  (let [local-video (r/atom video)]
    (fn []
      [:div.columns
       [:form
        [:div.row
         [:div.small-6.columns
          [:label "Título"
           [:input {:type "text"
                    :value (:title @local-video)
                    :on-change #(swap! local-video assoc :title (helper/get-target-value %))}]]
          (helper/errors-for (:errors video) :title)]
         [:div.small-6.columns
          [:label "Data"]
          [date-picker {:selected (:date @local-video)
                        :locale "pt-br"
                        :date-format "DD/MM/YYYY"
                        :on-change #(swap! local-video assoc :date %)}]
          (helper/errors-for (:errors video) :date)]]
        [:div.row
         [:div.small-12.columns
          [:label "Resumo"
           [:textarea {:on-change #(swap! local-video assoc :excerpt (helper/get-target-value %))
                       :rows 10
                       :value (:excerpt @local-video)}]]
          (helper/errors-for (:errors video) :excerpt)]]
        [:div.row.collapse
         [:div.small-6.columns
          [:div.small-12.columns
           [:label "Incorporar"
            [:input {:type "text"
                     :value (:embedded @local-video)
                     :on-change #(swap! local-video assoc :embedded (helper/get-target-value %))}]]
           (helper/errors-for (:errors video) :embedded)]
          [:div.small-12.columns
           [:label "Ativo?"]
           [:div.switch
            [:input#active {:type "checkbox"
                            :checked (:active? @local-video)
                            :on-change #(swap! local-video update :active? not)}]
            [:label {:for "active"}]]
           (helper/errors-for (:errors video) :active?)]]
         [:div.small-6.columns
          [:label "Preview do video"]
          [:div.flex-video.widescreen.youtube
           {:dangerouslySetInnerHTML {:__html (:embedded @local-video)}}]]]
        [:div.columns
         [:div.right
          [:button.small.alert {:type "button"
                                :on-click #(dispatch [:videos/toggle-editing video])}
           "Cancelar"]
          [:button.small {:type "button"
                          :on-click #(dispatch [:videos/edit @local-video])}
           "Salvar"]]]]])))

(defn collapsed-row [video]
  [:div.video-row.small-12.columns {:on-click #(dispatch [:videos/toggle-editing video])}
   [:div.status {:class (if (:active? video) "active" "inactive")}]
   [:div.small-2.columns.content-holder
    [:img.img-icon.small-3.columnns {:src "/images/video-icon.png"}]]
   [:div.small-8.columns.content-holder
    [:span.bottom-text (:title video)]]
   [:div.small-2.columns.content-holder
    [:span.bottom-text.light (br-date (:date video))]]])

(defn video-row [video]
  [:div.video-wrapper
   (if (:editing? video)
     [expanded-row video]
     [collapsed-row video])])

(defn video-list [videos]
  [:div.video-list.columns
   (for [video videos]
     ^{:key (random-uuid)}
     [video-row video])])

(defn videos-panel []
  (let [videos   (subscribe [:videos/all])
        new      (subscribe [:videos/new])
        loading? (subscribe [:videos/loading?])]
    (if @loading?
      [:div.large-12.columns
       [:i.fa.fa-spinner.fa-spin.fa-5x]]

      [:div.large-12.columns
       [:h1 "Vídeos "
        [:small "Cadastre vídeos para que eles apareçam no site"]]

       [:div.row
        [video-list @videos]]

       [:div.row
        [new-video @new]]])))
