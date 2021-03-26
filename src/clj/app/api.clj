(ns app.api
  (:require [reitit.coercion.spec]
            [spec-tools.data-spec :as ds]
            [clj-xlsx.schema :as xlsx-s]
            [app.handlers.user :as user-handler]
            [app.handlers.xlsx :as xlsx-handler]
            [app.mimetype :refer [mimetype-of]]
            [app.middleware :as mdw]
            [app.schema :as schema]
            [com.share.route :as route]))

(def route-handler-specs
  {::route/users            {:post {:summary    "Creates a new user in the database, returning their details"
                                    :middleware [[mdw/authenticated?]
                                                 [mdw/grant-access-as-role :admin]]
                                    :parameters {:header {:authorization schema/jwt-bearer?}
                                                 :body   {:username      string?
                                                          :password      string?
                                                          (ds/opt :role) keyword?}}
                                    :handler    user-handler/create-user-handler}}
   ::route/user             {:get {:summary    "Returning user details in database."
                                   :middleware [[mdw/authenticated?]
                                                [mdw/grant-authorized-user-or-admin]]
                                   :parameters {:header {:authorization schema/jwt-bearer?}
                                                :path   {:username string?}}
                                   :handler    user-handler/get-user-handler}}
   ::route/user-login       {:post {:summary    (str "Authenticates a user in the database, returning access token in body. "
                                                     "Set this token in cookies or header authorization to access api.")
                                    :parameters {:path {:username string?}
                                                 :body {:password string?}}
                                    :handler    user-handler/login-handler}}
   ;;::route/user-update  {:put {:summary    "Update user info"
   ;;                            :middleware [[mdw/authenticated?]]
   ;;                                         [mdw/grant-authorized-user-or-admin]]
   ;;                            :parameters {:header {:authorization schema/jwt-bearer?}
   ;;                                         :path   {:username string?}
   ;;                                         :body   {:password string?}}
   ;;                            :handler    user-handler/update-user-handler}}
   ::route/user-role        {:put {:summary    "Update user role"
                                   :middleware [[mdw/authenticated?]
                                                [mdw/grant-access-as-role :admin]]
                                   :parameters {:header {:authorization schema/jwt-bearer?}
                                                :path   {:username string?}
                                                :body   {:role keyword?}}
                                   :handler    user-handler/update-user-handler}}
   ::route/export-excel     {:post {:summary    "Export excel api"
                                    :middleware [[mdw/authenticated?]]
                                    :parameters {:header {:authorization schema/jwt-bearer?}
                                                 :body   xlsx-s/sheets}
                                    :handler    xlsx-handler/handler-export}}
   ::route/export-excel-get {:get {:summary    "Get exported excel file api"
                                   :swagger    {:produces [(mimetype-of ".xlsx")]}
                                   :parameters {:query  {:id string?}}
                                   :handler    xlsx-handler/handler-get-exported-file}}})