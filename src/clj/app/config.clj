(ns app.config
  (:require [cprop.core :as cprop]
            [mount.core :as m]))

(m/defstate config
  :start (cprop/load-config)
  :stop  nil)

(defn from-conf
  [path & [df]]
  (get-in config path df))
