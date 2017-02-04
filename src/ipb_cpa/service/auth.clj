(ns ipb-cpa.service.auth
  (:require [buddy.core
             [codecs :as codecs]
             [nonce :as nonce]]
            [buddy.hashers :as hasher]
            [clj-time
             [coerce :as c]
             [core :as t]
             [format :as f]]
            [clojure.java.jdbc :as j]
            [io.pedestal.interceptor.chain :as chain]
            [yesql.core :refer [defqueries]]
            [clojure.core.match :refer [match]]))

(defqueries "ipb_cpa/sql/users.sql")

(defn create-user [db user]
  (j/insert!
   db
   :users
   (-> user
       (update db :password hasher/encrypt)
       (update db :date (comp c/to-timestamp f/parse)))))

(defn user-by-email
  "Finds a user by email or return nil if there is no user with that email."
  [db email]
  (find-by-email
   {:email email}
   {:connection db
    :result-set-fn first}))

(defn random-token []
  (let [data (nonce/random-bytes 32)]
    (codecs/bytes->hex data)))

(defn add-token-to-user
  "Inserts a token to a user with expiration date set to 15 days from current day"
  [db user token]
  (j/insert!
   db
   :user_tokens
   {:token token
    :user_id (:id user)
    :expiration (-> 15 t/days t/from-now c/to-timestamp)}))

(defn authenticate!
  "Takes an email and a password and returns a pair where the first
  position is a boolean indicating whether the authentication succeeded
  or not, and the second element is the token on success or an error message
  if the first element is `false`"
  [db email password]
  (let [user     (user-by-email db email)
        matches? (hasher/check password (:password user))]
    (if matches?
      (let [token (random-token)]
        (add-token-to-user db user token)
        [true token])
      [false "Bad credentials"])))

(defn check-credentials [db token]
  (find-active-token {:token token} {:connection db
                                     :result-set-fn first}))

(def auth-interceptor
  {:name ::auth-interceptor
   :enter (fn [ctx]
            (let [token (-> ctx :request :query-params :token)
                  db    (-> ctx :request :system :database :db)]
              (if (check-credentials db token)
                ctx
                (chain/terminate ctx))))
   :leave (fn [ctx]
            (let [token (-> ctx :request :query-params :token)
                  db    (-> ctx :request :system :database :db)]
              (if (check-credentials db token)
                ctx
                (assoc-in ctx [:response :status] 403))))})

(defn ok [data]
  {:status 200
   :body data})

(defn unauthorized [data]
  {:status 403
   :body data})

(defn create-token
  [request]
  (let [db       (-> request :system :database :db)
        params   (:json-params request)
        email    (:email params)
        password (:password params)]
    (match (authenticate! db email password)
      [true token] (ok {:token token})
      [false message] (unauthorized {:message message}))))
