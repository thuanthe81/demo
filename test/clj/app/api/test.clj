(ns app.api.test
  (:require [clojure.test :refer :all]
            [clj-http.client]
            [app.config]
            [app.server]
            [mount.core :as m]))

(defn j-http
  [method url & [opts]]
  (let [f (->> (name method)
               symbol
               (ns-resolve 'clj-http.client))]
    (f url (merge opts
                  {:content-type :json
                   :as           :json}))))

(deftest ^:integration user-login
  (try
    (is (-> (j-http :post
                    "http://localhost:3000/api/users/defaultuser/login"
                    {:form-params {:password "password@123"}})
            (get-in [:body :jwt-token])
            some?))
    (is (-> (try (j-http :post
                         "http://localhost:3000/api/users/defaultuser/login"
                         {:form-params {:password "wrong-pass"}})
                 (catch Exception ex
                   (ex-data ex)))
            (get-in [:status])
            (= 401)))
    (is (-> (try (j-http :post
                         "http://localhost:3000/api/users/noneuser/login"
                         {:form-params {:password "wrong-pass"}})
                 (catch Exception ex
                   (ex-data ex)))
            (get-in [:status])
            (= 401)))
    (catch Exception ex
      (is false))))

(deftest ^:integration user-get-update
  ;; user role
  (let [jwt (-> (j-http :post
                        "http://localhost:3000/api/users/defaultuser/login"
                        {:form-params {:password "password@123"}})
                (get-in [:body :jwt-token]))]
    (is (= (-> (j-http :get "http://localhost:3000/api/users/defaultuser"
                       {:headers {:authorization (str "Bearer " jwt)}})
               :body
               (select-keys [:id :role]))
           {:id   "defaultuser"
            :role "user"}))
    (is (= (-> (try (j-http :get "http://localhost:3000/api/users/admin"
                            {:headers {:authorization (str "Bearer " jwt)}})
                    (catch Exception ex
                      (ex-data ex)))
               :status)
           400))
    (is (= (-> (try (j-http :put "http://localhost:3000/api/users/defaultuser/role"
                            {:headers     {:authorization (str "Bearer " jwt)}
                             :form-params {:role :admin}})
                    (catch Exception ex
                      (ex-data ex)))
               :status)
           400))
    (is (= (-> (try (j-http :post "http://localhost:3000/api/users"
                            {:headers     {:authorization (str "Bearer " jwt)}
                             :form-params {:username "newuser"
                                           :password "newuser@123"}})
                    (catch Exception ex
                      (ex-data ex)))
               :status)
           400)))
  ;; admin role
  (let [jwt (-> (j-http :post
                        "http://localhost:3000/api/users/admin/login"
                        {:form-params {:password "admin@123"}})
                (get-in [:body :jwt-token]))]
    (is (= (-> (j-http :get "http://localhost:3000/api/users/defaultuser"
                       {:headers {:authorization (str "Bearer " jwt)}})
               :body
               (select-keys [:id :role]))
           {:id   "defaultuser"
            :role "user"}))
    (is (= (-> (j-http :get "http://localhost:3000/api/users/admin"
                       {:headers {:authorization (str "Bearer " jwt)}})
               :body
               (select-keys [:id :role]))
           {:id   "admin"
            :role "admin"}))
    (is (= (-> (try (j-http :get "http://localhost:3000/api/users/noneuser"
                            {:headers {:authorization (str "Bearer " jwt)}})
                    (catch Exception ex
                      (ex-data ex)))
               :status)
           400))
    (is (= (-> (try (j-http :post "http://localhost:3000/api/users"
                            {:headers     {:authorization (str "Bearer " jwt)}
                             :form-params {:username "newuser"
                                           :password "newUSER@123"}})
                    (catch Exception ex
                      (ex-data ex)))
               :body
               (select-keys [:id :role]))
           {:id   "newuser"
            :role "user"}))
    (is (= (-> (try (j-http :get "http://localhost:3000/api/users/newuser"
                            {:headers     {:authorization (str "Bearer " jwt)}})
                    (catch Exception ex
                      (ex-data ex)))
               :body
               (select-keys [:id :role]))
           {:id   "newuser"
            :role "user"}))
    (is (= (-> (try (j-http :put "http://localhost:3000/api/users/newuser/role"
                            {:headers     {:authorization (str "Bearer " jwt)}
                             :form-params {:role :admin}})
                    (catch Exception ex
                      (ex-data ex)))
               :body
               (select-keys [:id :role]))
           {:id   "newuser"
            :role "admin"}))
    (swap! app.auth/user-database dissoc "newuser")))
