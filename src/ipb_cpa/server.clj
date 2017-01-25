(ns ipb-cpa.server
  (:gen-class) ; for -main method in uberjar
  (:require [ipb-cpa.system :refer [system]]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (component/start
   (system {:web-server     {:port 80
                             :env  :prod}
            :connection-uri (env :connection-uri)
            :mailer-params  {:host (env :smtp-host)
                             :port (env :smtp-port)
                             :user (env :smtp-user)
                             :pass (env :smtp-pass)}})))
