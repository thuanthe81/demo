(ns app.handlers.user
  (:require [app.auth :as auth]
            [app.jwt :as jwt])
  (:import (java.util Date)))


(defn create-user-handler
  "A web handler to create a new user"
  [{{{:keys [username password role]} :body} :parameters}]
  (try
    {:status 200
     :body   (if role
               (auth/create-user auth/user-database username password role)
               (auth/create-user auth/user-database username password))}
    (catch Exception e
      {:status 400
       :body   (ex-data e)})))

(defn validate-jwt-token
  [jwt-token]
  "Return user when jwt-token is valid."
  (let [{:as   user
         :keys [id
                login-at]} (jwt/->data jwt-token)
        {:keys [last-update]} (when id
                                (auth/get-user @auth/user-database id))]
    (cond
      (nil? last-update)
      (throw (ex-info "Bad request" {:un-authorized "Your account not existed!"}))

      (< login-at last-update)
      (throw (ex-info "Bad request" {:un-authorized "Session was expired!"}))

      :else
      (assoc user :role (-> user :role keyword)))))

(defn login-handler
  "A web handler to get access user-api token."
  [{{{:keys [password]} :body
     {:keys [username]} :path} :parameters}]
  (try
    (let [jwt-token (-> (auth/authenticate-user @auth/user-database username password)
                        (select-keys [:id :role :last-update])
                        (assoc :login-at (System/currentTimeMillis))
                        jwt/->jwt-token)]
      {:status 200
       :body   {:jwt-token jwt-token}})
    (catch Exception e
      {:status 401
       :body   (ex-data e)})))

(defn get-user-handler
  "A web handler to authenticate an existing user"
  [{{{:keys [username]} :path} :parameters}]
  (try
    (if-let [user (auth/get-user @auth/user-database username)]
      {:status 200
       :body   user}
      {:status 400
       :body {:bad-request "User is not existed."}})
    (catch Exception e
      {:status 401
       :body   (ex-data e)})))

(defn update-user-handler
  "A web handler to update an existing user"
  [{{user-info          :body
     {:keys [username]} :path} :parameters :as params}]
  (try
    (if-let [user (get @auth/user-database username)]
      {:status 200
       :body   (auth/update-user auth/user-database username user-info)}
      {:status 400
       :body   {:bad-request "Not found user."}})
    (catch Exception e
      {:status 401
       :body   (ex-data e)})))