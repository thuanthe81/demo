(ns demo.events
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [ajax.core :as ajax]
            [akiroz.re-frame.storage :refer [reg-co-fx! persist-db]]
            [demo.db :as db]
            [demo.router :as router]
            [com.share.route :as route]
            [demo.utils :as utils]
            [demo.page.home.events]
            [clojure.walk :refer [keywordize-keys]]
            day8.re-frame.http-fx
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx reg-cofx inject-cofx ->interceptor]]
            ["jwt-decode" :as jwt-decode]))

(reg-co-fx!
  :demo
  {:fx   :store
   :cofx :store})


(reg-event-fx
 :events/demo.initialize-db
 [(inject-cofx :store)]
 (fn [{:keys [store]} _]
   (let [token (:token store)]
     (when token
       (js/setTimeout (fn []
                        (rf/dispatch [:events/sign-in.refresh-token]))
                      100))
     {:db         (assoc db/default-db
                         :user-token token
                         :user-info  (some-> token (jwt-decode) (js->clj :keywordize-keys true)))
      :store      store
      :dispatch (if token
                   [:events/demo.navigate-to :page.home]
                   [:events/demo.navigate-to :page.sign-in])})))


(reg-event-fx
  :events/demo.hook-session-expired
  (fn [{:keys [db]} [_ failure-handler error]]
    (cond
      (-> error :status (= 401))
      {:dispatch [:events/account.signout]}

      failure-handler
      {:dispatch (conj failure-handler error)})))


(reg-event-fx
  :events/demo.navigate-to
  (fn [{db :db} [_ page]]
    (let [{:keys [events]} (router/page-id->settings page)]
      (merge {:db (assoc db :active-page page)}
             (when (not-empty events)
               {:dispatch-n events})))))


;;----- Sign In ------
(reg-event-fx
 :events/sign-in.submit
 (fn [{db :db} [_ {:keys [username password]}]]
   {:http-xhrio (merge utils/common-http-xhrio
                       {:method          :post
                        :uri             (router/api-id->uri ::route/user-login
                                                             {:path-params {:username username}})
                        :headers         (utils/transform-headers)
                        :params          {:password password}
                        :on-success      [:events/sign-in.success]
                        :on-failure      [:events/sign-in.failure]})}))


(reg-event-fx
 :events/sign-in.success
 [(inject-cofx :store)]
 (fn [{:keys [db store]} [_ {:keys [jwt-token]}]]
   {:db                  (assoc db :user-token jwt-token)
    :store               (assoc store :token jwt-token)
    :dispatch-n          (list [:events/sign-in.update-token jwt-token]
                               [:events/demo.navigate-to :page.home])}))


(reg-event-db
 :events/sign-in.failure
 (fn [db [_ error]]
   (js/console.log error)))


(reg-event-db
  :events/sign-in.update-token
  (fn [db [_ token]]
    (let [user-info (when token
                      (-> token (jwt-decode) (js->clj :keywordize-keys true)))]
      (assoc db
             :user-token token
             :user-info user-info))))


(reg-event-fx
  :events/sign-in.refresh-token
  (fn [{db :db} _]
    (when (:user-token db)
      {:http-xhrio {:method          :get
                    :uri             (router/api-id->uri ::route/user-refresh-token
                                                         {:path-params {:username (-> db :user-info :id)}})
                    :headers         (utils/transform-headers {:user-token (:user-token db)})
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:events/sign-in.update-token]
                    :on-failure      [:events/demo.hook-session-expired [:events/sign-in.failure]]}})))


(reg-event-fx
  :events/account.signout
  [(inject-cofx :store)]
  (fn [{:keys [db store]} [_ response]]
    {:db         db/default-db
     :store      (dissoc store :token)
     :dispatch-n (list [:events/demo.navigate-to :page.sign-in])}))

