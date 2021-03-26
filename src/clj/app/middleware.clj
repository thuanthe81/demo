(ns app.middleware
  (:require [app.config :refer [from-conf]]
            [reitit.core :as r]
            [buddy.auth.http :as http]
            [app.jwt :as jwt]
            [app.schema :as schema]
            [ring.middleware.cookies :as r.m.cookies]
            [clojure.string :as string]
            [clojure.spec.alpha :as spec]
            [cheshire.core :as json]
            [app.handlers.user :as user-handler]))


(defn request->token
  [request]
  (-> request
      (get-in [:parameters :header :authorization])
      schema/bearer->jwt-token))


(defn authenticated?
  [handler]
  (fn [request]
    (try
      (if-let [auth-user (some->> request
                                  request->token
                                  user-handler/validate-jwt-token)]
        (->> (assoc auth-user :role (-> auth-user
                                        :role
                                        keyword))
             (assoc request :auth-user)
             handler)
        {:status 403
         :body {:un-authorized "Unauthorized"}})
      (catch Exception ex
        (let [d (ex-data ex)]
          (if (:un-authorized d)
            {:status 403
             :body   d}
            (throw ex)))))))

(def roles [:admin
            :user])
(def min-role (last roles))

(defn grant-access-as-role
  [handler role]
  (fn [request]
    (let [auth-role (-> request :auth-user :role)]
      (cond
        (= auth-role :admin) (handler request)
        (= auth-role role) (handler request)
        :else {:status 400
               :body   {:bad-request (str "Require you as " (name role))}}) )))

(defn grant-authorized-user-or-admin
  [handler]
  (fn [request]
    (let [{auth-username :id
           auth-role     :role} (:auth-user request)
          request-username (-> request :parameters :path :username)]
      (cond
        (= auth-username
           request-username) (handler request)
        (= auth-role :admin) (handler request)
        :else {:status 400
               :body   {:bad-request "Require you as admin"}}) )))
