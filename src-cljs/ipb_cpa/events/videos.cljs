(ns ipb-cpa.events.videos
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [com.rpl.specter :as specter :refer [transform setval ALL]]
            [ajax.core :as ajax]))

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

(reg-event-fx
 :videos/load-videos
 (fn [{:keys [db]} _]
   {:db (assoc db :videos/loading? true)
    :http-xhrio {:method :get
                 :uri (str "/api/videos?token=" (:token db))
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:videos/loaded-successfully]
                 :on-failure [:videos/load-failed]}}))

(defn ->moment [datestr]
  (js/moment datestr))

(reg-event-db
 :videos/loaded-successfully
 (fn [db [_ videos]]
   (let [videos-with-moment-dates (map #(update % :date ->moment) videos)]
     (-> db
         (assoc :videos/loading? false)
         (assoc :videos/videos videos-with-moment-dates)))))

(reg-event-db
 :videos/load-failed
 (fn [db [_ err]]
   (println "Error:" err)
   (assoc db :videos/loading? false)))
