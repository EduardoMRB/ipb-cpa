(ns ipb-cpa.site.view
  (:require [hiccup.page :refer [html5]]
            [io.pedestal.http.route :refer [url-for]]))

(defn index []
  (html5 {:lang "pt-br"}
         [:head
          [:title "Igreja Presbiteriana do CPA IV"]
          [:meta {:name :view-port
                  :context "width=device-width, initial-scale=1"}]
          [:link {:href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css"
                  :rel "stylesheet"}]
          [:body
           [:div.container
            ;; header.
            [:div.row
             [:a {:href (url-for :site#index)}
              [:img {:source "images/ipb-logo.png"}]]
             [:nav.top-nav
              [:ul.top-menu
               [:li [:a {:href "#"} "Sobre"]]
               [:li [:a {:href "#"} "Programação"]]
               [:li [:a {:href "#"} "Mensagens e Estudos"]]
               [:li [:a {:href "#"} "Fale Conosco"]]
               [:li [:a {:href "#"} "Missões"]]]]]
            ;; Site content.
            [:main.row.site-content]
            ;; 
            [:footer.site-footer]
           ;; JavaScript resources.
           [:script {:src "http://code.jquery.com/jquery-2.1.0.min.js"}]
           [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"}]]]]))
