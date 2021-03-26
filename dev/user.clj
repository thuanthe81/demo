(ns user
  (:require [app.core]
            [mount.core :as m]))

(defn go []
  (m/start))

(defn restart []
  (m/stop)
  (m/start))
