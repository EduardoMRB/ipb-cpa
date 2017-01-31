(ns ipb-cpa.events.videos
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [com.rpl.specter :as specter :refer [transform setval ALL]]))

(defn video-path [video]
  [:videos/videos ALL #(= (:id %) (:id video))])

(reg-event-db
 :videos/toggle-editing
 (fn [db [_ video]]
   (transform (video-path video)
              (fn [video]
                (update video :editing? not))
              db)))

(reg-event-db
 :videos/set-new
 (fn [db [_ attr val]]
   (assoc-in db [:videos/new attr] val)))

(reg-event-db
 :videos/edit
 (fn [db [_ video]]
   (setval (video-path video)
           (assoc video :editing? false)
           db)))

(def default-new
  {:title ""
   :date nil
   :excerpt ""
   :embedded ""
   :active? true
   :errors nil
   :editing false})

(reg-event-db
 :videos/create
 (fn [db _]
   (let [video (:videos/new db)]
     (-> db
         (update :videos/videos conj video)
         (assoc :videos/new default-new)))))
