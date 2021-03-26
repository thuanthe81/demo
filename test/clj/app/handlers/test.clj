(ns app.handlers.test
  (:require [clojure.test :refer :all]
            [app.handlers.user :as user-handlers]))

(def users {:user     {:username "defaultuser"
                       :password "password@123"}
            :new-user {:username "testuser"
                       :password "testuser@123"}
            :admin    {:username "admin"
                       :password "admin@123"}})

(defn get-token
  [user-type]
  (let [{:keys [username
                password]} (user-type users)]
    (->> (user-handlers/login-handler {:parameters {:path {:username username}
                                                   :body {:password password}}})
         :body
         :jwt-token
         (hash-map :username username
                   :token))))


(deftest user-get-user
  (let [{:keys [username
                token]} (get-token :user)
        user-info (user-handlers/get-user-handler {:auth-user {:id username}
                                                   :parameters {:path {:username username}}})
        auth-user-info (user-handlers/get-user-handler {:auth-user {:id username}
                                                        :parameters {:path {:username username}}})]
    (is (some? user-info))
    (is (some? auth-user-info))
    (is (= username
           (-> user-info :body :id)))
    (is (= username
           (-> auth-user-info :body :id)))))

