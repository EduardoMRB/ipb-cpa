(defproject ipb-cpa "0.0.1-SNAPSHOT"
  :description "First presbyterian church of CPA IV's website."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]

                 [io.pedestal/pedestal.service "0.5.2"]
                 [io.pedestal/pedestal.jetty "0.5.2"]

                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.slf4j/log4j-over-slf4j "1.7.7"]

                 [cheshire "5.7.0"]
                 [cljs-ajax "0.5.8"]
                 [bouncer "0.3.3"]
                 [domina "1.0.3"]

                 [com.taoensso/carmine "2.10.0"]

                 [instaparse "1.4.0"]
                 [yesql "0.5.3"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ragtime "0.5.1"]
                 [environ "1.0.0"]

                 [com.andrewmcveigh/cljs-time "0.4.0"]

                 [com.stuartsierra/component "0.3.1"]

                 [hiccup "1.0.5"]
                 [com.draines/postal "2.0.0"]

                 [reagent "0.6.0"]
                 [re-frame "0.9.1"]
                 [secretary "1.2.3"]
                 [day8.re-frame/http-fx "0.1.3"]

                 [com.rpl/specter "0.13.2"]
                 [cljsjs/react-input-mask "0.7.5-0"]
                 [cljsjs/react-datepicker "0.29.0-0"]
                 [buddy "1.3.0"]
                 [buddy/buddy-hashers "1.2.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [com.pupeno/free-form "0.5.0"]]
  :clean-targets ^{:protect false} [:profile-path :compile-path "out" "resources/public/js/compiled/out"]
  :figwheel {:css-dirs ["resources/public/css"]}
  :plugins [[lein-cljsbuild "1.1.5"]]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :figwheel {:on-jsload "ipb-cpa.core/mount-root"}
                        :compiler {:main ipb-cpa.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}]}
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:plugins [[lein-figwheel "0.5.8"]]
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.2"]
                                  [midje "1.8.3"]
                                  [org.clojure/tools.namespace "0.2.10"]
                                  [org.xerial/sqlite-jdbc "3.7.2"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [binaryage/devtools "0.9.0"]]
                   :source-paths ["dev"]}
             :uberjar {:aot [ipb-cpa.server]}
             :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                            :init-ns user}}
  :main ipb-cpa.server)
