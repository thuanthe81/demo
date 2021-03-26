(ns app.zz
  (:require [clojure.test :refer :all]
            [app.config :as config]
            [app.server :as server]
            [mount.core :as m]))

(deftest test-stop
  (m/stop #'server/server
          #'config/config))