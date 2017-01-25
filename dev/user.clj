(ns user
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [clojure.string :as s]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [midje.repl :refer [autotest]]
            [ragtime.repl :as rrepl]
            [ragtime.jdbc :as jdbc]
            [environ.core :as env :refer [env]]
            [ipb-cpa.server :as server]
            [figwheel-sidecar.repl-api :refer :all]
            [ipb-cpa.system :as system]
            [com.stuartsierra.component :as component]))

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

(defn cljs-start []
  (start-figwheel!)
  (cljs-repl))

(def ^:dynamic sys nil)

(defn init []
  (alter-var-root
   #'sys
   (constantly
    (system/system
     {:web-server     {:port 8080
                       :env  :dev}
      :connection-uri (env :db-connection-uri)
      :mailer-params  {:host (env :smtp-host)
                       :port (env :smtp-port)
                       :user (env :smtp-user)
                       :pass (env :smtp-pass)}}))))

(defn start []
  (alter-var-root #'sys component/start))

(defn stop []
  (alter-var-root #'sys component/stop))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
