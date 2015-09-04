(ns user
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [clojure.string :as s]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [midje.repl :refer [autotest]]
            [ragtime.repl :as rrepl]
            [ragtime.jdbc :as jdbc]
            [environ.core :as env :refer [env]]))

(def config
  {:datastore  (jdbc/sql-database {:connection-uri (env :db-connection-uri)})
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  "Run database migrations"
  (rrepl/migrate config))

(defn rollback
  ([] (rrepl/rollback config))
  ([amount-or-id]
   (rrepl/rollback config amount-or-id)))
