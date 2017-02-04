(ns ipb-cpa.components.login
  (:require [free-form.core :as free-form]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [ipb-cpa.helper :as helper]))

(defn login-screen []
  (let [values (subscribe [:login/data])
        errors (subscribe [:login/errors])]
    [:div.row
     [:div.large-4.large-offset-4.columns
      [:img {:src "/images/ipb-logo-without-text.png"}]
      [free-form/form @values @errors (fn [keys value] (dispatch [:login/set-attr keys value]))
       [:div
        [:div.errors {:free-form/error-message {:key :general}}
         [:small.error]]
        [:div.row
         [:div.large-12.columns
          [:label "Email"
           [:input {:free-form/input       {:key :email}
                    :free-form/error-class {:key :email :error "error"}
                    :placeholder           "email@exemplo.com"
                    :on-key-press          (helper/on-enter #(dispatch [:login/submit]))}]
           [:div.errors {:free-form/error-message {:key :email}}
            [:small.error]]]]]
        [:div.row
         [:div.large-12.columns
          [:label "Senha"
           [:input {:free-form/input       {:key :password}
                    :free-form/error-class {:key :password :error "error"}
                    :type                  :password
                    :on-key-press          (helper/on-enter #(dispatch [:login/submit]))}]
           [:div.errors {:free-form/error-message {:key :password}}
            [:small.error]]]]]
        [:div.row
         [:div.large-12.columns
          [:button.medium.success.expand {:on-click #(dispatch [:login/submit])}
           "Entrar"]]]]]]]))
