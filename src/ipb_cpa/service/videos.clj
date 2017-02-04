(ns ipb-cpa.service.videos
  (:require [clj-time
             [coerce :as c]
             [core :as t]
             [format :as f]]
            [ring.util.response :as ring-resp]
            [yesql.core :refer [defqueries]]))

(defqueries "ipb_cpa/sql/videos.sql")

(defn- ->sql-date [datestr]
  (c/to-sql-date
   (f/parse (f/formatters :year-month-day) datestr)))

(defn get-videos [request]
  (let [db (get-in request [:system :database :db])]
    (ring-resp/response (select-all-videos {} {:connection db}))))

(defn create [request]
  (let [db     (get-in request [:system :database :db])
        video  (update (:json-params request) :date ->sql-date)
        result (apply insert-video<! video {:connection db})]
    (ring-resp/response {:ok    true
                         :video result})))

(defn delete [request]
  (let [db (get-in request [:system :database :db])
        id (-> request :path-params :id Integer/parseInt)]
    (delete-video! {:id id} {:connection db})
    (ring-resp/response {:ok true
                         :id id})))

(defn change [request]
  (let [db     (get-in request [:system :database :db])
        video  (update (:json-params request) :date ->sql-date)
        result (update-video<! video {:connection db})]
    (ring-resp/response
     {:ok    true
      :video result})))
