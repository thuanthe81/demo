(ns app.aa
  (:require [clojure.test :refer :all]
            [app.config :as config]
            [app.server :as server]
            [mount.core :as m]))

(deftest test-start
  (m/start #'config/config
           #'server/server))