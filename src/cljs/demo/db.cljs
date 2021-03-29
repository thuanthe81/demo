(ns demo.db
  (:require [clojure.string :as str]))

(def app-info
  {:name    "Demo app"
   :version "1.0.0"})


(def local-storage (-> app-info
                       :name
                       (str/replace #" " "-")
                       str/lower-case))


(def default-db
  {:user-info          nil
   :user-token        nil
   :active-page       :page.sign-in})


(defn ->user-data [db] (:user-info db))