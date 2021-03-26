(ns app.test
  (:require [clojure.test :refer :all]
            [app.jwt :as jwt]
            [app.middleware :as mdw]
            [app.handlers.user :as user]))


(defn do-jwt
  [data]
  (-> data jwt/->jwt-token jwt/->data))

(deftest jwt-1
  (let [data  {:a 1 :b "test"}
        after (do-jwt data)]
    (is (and (= (:a data)
                (:a after))
             (= (:b data)
                (:b after))))))

(deftest jwt-2
  (let [data  {:a nil :b (System/currentTimeMillis)}
        after (do-jwt data)]
    (is (and (nil? (:a after))
             (= (-> data :b)
                (-> after :b))))))


(def forward-handler identity)

(deftest mdw-authenticated
  (let [token     (-> (user/login-handler {:parameters {:path {:username "defaultuser"}
                                                        :body {:password "password@123"}}})
                      :body)
        valid-h   (fn [username]
                    (fn [{:keys [auth-user]}]
                      (is (= (:id auth-user)
                             username))))
        invalid-h (fn [{:keys [body]}]
                    (is (-> body :un-authorized some?)))]
    (is (-> token :jwt-token some?))
    (-> ((mdw/authenticated? forward-handler)
         {:parameters {:header {:authorization (str "Bearer " (:jwt-token token))}}})
        ((fn [x] ((valid-h "defaultuser") x))))
    (-> ((mdw/authenticated? forward-handler)
         {:parameters {:header {}}})
        invalid-h)))

(deftest mdw-grant-access-as-role
  (let [valid-h   (fn [{:keys [body]}]
                    (is (nil? body)))
        invalid-h (fn [{:keys [body]}]
                    (is (-> body :bad-request some?)))]
    (-> ((mdw/grant-access-as-role forward-handler :admin)
         {:auth-user {:id   "defaultuser"
                      :role :user}})
        invalid-h)
    (-> ((mdw/grant-access-as-role valid-h :admin)
         {:auth-user {:id   "defaultuser"
                      :role :admin}})
        valid-h)
    ((mdw/grant-access-as-role valid-h :user)
     {:auth-user {:id   "defaultuser"
                  :role :user}})))

(deftest mdw-grant-authorized-user-or-admin
  (let [valid-h   (fn [{:keys [body] :as resp}]
                    (is (nil? body)))
        invalid-h (fn [{:keys [body] :as resp}]
                    (is (-> body :bad-request some?)))]
    (-> ((mdw/grant-authorized-user-or-admin forward-handler)
         {:auth-user  {:id   "defaultuser"
                       :role :user}
          :parameters {:path {:username "defaultuser"}}})
        valid-h)
    (-> ((mdw/grant-authorized-user-or-admin forward-handler)
         {:auth-user  {:id   "admin"
                       :role :admin}
          :parameters {:path {:username "defaultuser"}}})
        valid-h)
    (-> ((mdw/grant-authorized-user-or-admin forward-handler)
         {:auth-user  {:id   "defaultuser"
                       :role :user}
          :parameters {:path {:username "admin"}}})
        invalid-h)))