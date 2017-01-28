(ns ipb-cpa.db)

(def app-db
  {:active-panel       nil
   :schedules          []
   :editing-schedules  {}
   :deleting-schedules {}
   :new-schedule       {:description ""
                        :time        ""
                        :day_of_the_week "Segunda"}
   :days-of-the-week   (array-map
                        :seg true
                        :ter false
                        :quar false
                        :quin false
                        :sex false
                        :sab false
                        :dom false)})
