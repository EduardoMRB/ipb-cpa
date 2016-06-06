(ns ipb-cpa.mail
  (:require [postal.core :as postal]
            [clojure.core.match :refer [match]]))

(defprotocol IMailer
  (send-mail [this to from message]))

(defn postal-send-mail
  "Sends a mail message with postal"
  [mailer to from message]
  (match (postal/send-message
          mailer
          (-> message
              (assoc :from from)
              (assoc :to "pastor@gmail.com")))
    {:error :SUCCESS} true
    :else (throw (ex-info "Could not send email, please, try again later"
                          {:mailer mailer
                           :from from
                           :message message}))))
