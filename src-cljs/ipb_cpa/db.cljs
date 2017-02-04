(ns ipb-cpa.db)

(def app-db
  {:active-panel nil
   :token        nil

   :login/data   {:email    ""
                  :password ""}
   :login/errors {:general []}

   :schedules                []
   :editing-schedules        {}
   :editing-schedules-errors {}
   :deleting-schedules       {}
   :new-schedule             {:description     ""
                              :time            ""
                              :day_of_the_week "Segunda"}
   :days-of-the-week         (array-map
                              :seg true
                              :ter false
                              :quar false
                              :quin false
                              :sex false
                              :sab false
                              :dom false)

   :videos/loading? false
   :videos/videos   []
   :videos/new      {:title    ""
                     :date     nil
                     :excerpt  ""
                     :embedded ""
                     :active?  true
                     :errors   nil
                     :editing  false}})
