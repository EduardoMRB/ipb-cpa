(ns ipb-cpa.db)

(def app-db
  {:active-panel             nil
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

   :videos/videos [{:id 1
                    :title "Literatura e Cristianismo"
                    :date (js/moment "2015-11-11")
                    :excerpt "Literatura e Cristianiso soa assuntos nao tao distantes como a
                            maioria gosta de pensar, nesse video veremos um pouco sobre a
                            historia do cristianismo na literatura"
                    :embedded "<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/5jpRRcoeQ2Y\" frameborder=\"0\" allowfullscreen></iframe>"
                    :active? true
                    :editing? false}
                   {:id 2
                    :title "Literatura e Cristianismo"
                    :date (js/moment "2015-04-11")
                    :excerpt "Literatura e Cristianiso soa assuntos nao tao distantes como a
                            maioria gosta de pensar, nesse video veremos um pouco sobre a
                            historia do cristianismo na literatura"
                    :embedded "<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/5jpRRcoeQ2Y\" frameborder=\"0\" allowfullscreen></iframe>"
                    :active? false
                    :editing? false}]

   :videos/new {:title ""
                :date nil
                :excerpt ""
                :embedded ""
                :active? true
                :errors nil
                :editing false}})
