(ns app.jwt
  (:require [buddy.sign.jwt :as jwt]
            [app.config :refer [from-conf]]))

(defn ->jwt-token
  [data]
  (->> [:jwt-secret]
       from-conf
       (jwt/sign data)))

(defn ->data
  [jwt-token]
  (->> [:jwt-secret]
       from-conf
       (jwt/unsign jwt-token)))
