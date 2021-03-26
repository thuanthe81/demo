(ns app.schema)

(defn bearer->jwt-token
  [bearer]
  (some->> bearer
           (re-find #"((?i)bearer[\s]+)(.*)$")
           last))

(defn jwt-bearer?
  [bearer]
  (-> bearer
      bearer->jwt-token
      some?))