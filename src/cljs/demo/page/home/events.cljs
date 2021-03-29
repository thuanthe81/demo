(ns demo.page.home.events
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
            [com.share.route :as route]
            [demo.router :as router]
            [demo.utils :as utils]))


(reg-event-fx
  :events/home.get-all-users
  (fn [{{:keys [user-token]} :db} [_]]
    {:http-xhrio (merge utils/common-http-xhrio
                        {:method     :get
                         :uri        (router/api-id->uri ::route/users)
                         :headers    (utils/transform-headers {:user-token user-token})
                         :on-success [:events/home.get-all-users-success]
                         :on-failure [:events/home.get-all-user-failure]})}))


(reg-event-db
  :events/home.get-all-users-success
  (fn [db [_ users-resp]]
    (assoc-in db [:home :users] (:users users-resp))))


(reg-event-db
  :events/home.get-all-user-failure
  (fn [db [_ error]]
    (js/console.log "error" error)))

(defn users->sheets
  [users]
  (let [cnt (count users)]
    [{:name "Users"
      :rows (-> [[{:v "User"} {:v "Role"}]]
                (concat (->> users
                             (mapv (fn [u]
                                     [{:v (:id u)} {:v (:role u)}]))))
                (concat [[]
                         []
                         []
                         [{:v "Summary"}]
                         [{:v "number user"} {:f (str "COUNTIF(B2:B" (+ cnt 1) ", \"user\")")}]
                         [{:v "number admin"} {:f (str "COUNTIF(B2:B" (+ cnt 1) ", \"admin\")")}]])
                vec)}]))

(reg-event-fx
  :events/home.export-users
  (fn [{db :db} _]
    {:db (assoc-in db [:home :export-file :users] :processing)
     :http-xhrio (merge utils/common-http-xhrio
                        {:method     :post
                         :uri        (router/api-id->uri ::route/export-excel)
                         :headers    (utils/transform-headers {:user-token (:user-token db)})
                         :params     (-> db :home :users users->sheets)
                         :on-success [:events/home.export-users-success]
                         :on-failure [:events/demo.hook-session-expired
                                      [:events/home.export-users-failure]]})}))

(reg-event-db
  :events/home.export-users-success
  (fn [db [_ resp]]
    (assoc-in db [:home :export-file :users] resp)))

(reg-event-db
  :events/home.export-users-failure
  (fn [db [_ resp]]
    (assoc-in db [:home :export-file] nil)))


(reg-event-fx
  :events/home.update-user-role
  (fn [{db :db} [_ role-info]]
    (let [users   (-> db :home :users)
          updated (->> users
                       (map (fn [u]
                              (cond-> u
                                      (= (:id u)
                                         (:id role-info))
                                      (assoc :role (:role role-info)))))
                       vec)]
      {:db         (assoc-in db [:home :users] updated)
       :http-xhrio (merge utils/common-http-xhrio
                          {:method     :put
                           :uri        (router/api-id->uri ::route/user-role
                                                           {:path-params {:username (:id role-info)}})
                           :headers    (utils/transform-headers {:user-token (:user-token db)})
                           :params     {:role (:role role-info)}
                           :on-success [:events/home.update-user-role-success role-info]
                           :on-failure [:events/demo.hook-session-expired
                                        [:events/home.update-user-role-failure]]})})))


(reg-event-db
  :events/home.update-user-role-success
  (fn [db [_ role-info resp]]
    ;; do nothing
    (let [users   (-> db :home :users)
          updated (->> users
                       (map (fn [u]
                              (if (= (:id u)
                                     (:id role-info))
                                resp
                                u)))
                       vec)]
      (assoc-in db [:home :users] updated))))


(reg-event-fx
  :events/home.update-user-role-failure
  (fn [_ _]
    ;; simple roleback
    {:dispatch [:events/home.get-all-users]}))