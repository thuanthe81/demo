(ns app.core
  (:require [app.config :refer [config]]
            [app.server :refer [server]]
            [mount.core :as m]))


(defn -main [& args]
  (m/start #'config
           #'server))

