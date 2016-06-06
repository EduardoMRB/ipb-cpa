(ns ipb-cpa.test-system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [ipb-cpa
             [mail :as mail]
             [system :as system]]))

;; Keeps the last message "sent" by the TestMailer component
(defonce last-message (atom nil))

(defrecord TestMailer [host port user pass]
  component/Lifecycle
  (start [this] this)
  (stop [this] this)

  mail/IMailer
  (send-mail [this to from message]
    (let [full-message (-> message
                           (assoc :to to)
                           (assoc :from from))]
      (reset! last-message full-message)
      (assoc this :last-message full-message))))

(defn make-test-mailer [{:keys [host port user pass]}]
  (->TestMailer host port user pass))

(defn test-system []
  (component/system-map
   :database (system/make-database (env :test-db-connection-uri))
   :mailer (make-test-mailer {:host :noop :port :noop
                              :user :noop :pass :noop})))
