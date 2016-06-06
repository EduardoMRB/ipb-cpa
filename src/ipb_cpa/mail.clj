(ns ipb-cpa.mail
  (:require [postal.core :as postal]
            [clojure.core.match :refer [match]]))

(defn send-mail
  "Sends a mail message with postal"
  [mailer from message]
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
