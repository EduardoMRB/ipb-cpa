(defproject ipb-cpa "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [io.pedestal/pedestal.service "0.4.0"]
                 [org.omcljs/om "0.8.8"]
                 [com.cemerick/clojurescript.test "0.3.3"]
                 [sablono "0.3.4"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.4.0"]
                 ;; [io.pedestal/pedestal.immutant "0.4.0"]
                 ;; [io.pedestal/pedestal.tomcat "0.4.0"]

                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.slf4j/log4j-over-slf4j "1.7.7"]

                 [cheshire "5.5.0"]
                 [cljs-ajax "0.3.13"]
                 [bouncer "0.3.3"]
                 [domina "1.0.3"]

                 [com.taoensso/carmine "2.10.0"]

                 [ns-tracker "0.3.0"]

                 [instaparse "1.4.0"]
                 [yesql "0.4.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ragtime "0.5.1"]
                 [environ "1.0.0"]

                 [com.andrewmcveigh/cljs-time "0.3.7"]

                 [com.stuartsierra/component "0.2.3"]

                 [hiccup "1.0.5"]]
  :clean-targets ^{:protect false} [:profile-path :compile-path "out" "resources/public/js/out"]
  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.5"]]
  :figwheel {:http-server-root "public"
             :css-dirs ["resources/public/css"]
             :nrepl-port 7888}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs" "test-cljs"]
                        :figwheel true
                        :compiler {:output-to "resources/public/js/app.js"
                                   :output-dir "resources/public/js/out"
                                   :asset-path "js/out"
                                   :optimizations :none
                                   :pretty-print true}}]
              :test-commands {"phantom" ["phantomjs"
                                         "resources/public/js/out/goog/base.js"
                                         "resources/public/js/app.js"]}}
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "ipb-cpa.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.4.0"]
                                  [midje "1.7.0-beta1"]
                                  [org.clojure/tools.namespace "0.2.10"]
                                  [org.xerial/sqlite-jdbc "3.7.2"]
                                  [figwheel-sidecar "0.5.2"]]
                   :source-paths ["dev"]}
             :uberjar {:aot [ipb-cpa.server]}})
