(ns ipb-cpa.db)

(def app-db
  {:active-panel     nil
   :schedules        []
   :editing-schedules {}
   :deleting-schedules {}
   :days-of-the-week (array-map
                      :seg true
                      :ter false
                      :quar false
                      :quin false
                      :sex false
                      :sab false
                      :dom false)})
