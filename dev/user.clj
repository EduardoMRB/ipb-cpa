(ns user
  (:require [clojure.repl :refer :all]
            [midje.repl :refer [autotest]]
            [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]))
