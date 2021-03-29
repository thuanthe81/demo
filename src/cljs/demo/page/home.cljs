(ns demo.page.home
  (:require [re-frame.core :refer [subscribe dispatch]]
            [demo.router :as router]
            [com.share.route :as route]
            [demo.components :as components]))

(defn home-page []
  (let [users @(subscribe [:subs/home.users])
        auth-user @(subscribe [:subs/user.info])
        file-resp @(subscribe [:subs/home.export-file-users])]
    [:div {:style {:padding-left "300px"}}
     [:div "Users"]
     [:div
      [:table.table-users
       [:thead
        [:tr [:th "User name"] [:th "role"]]]
       [:tbody
        (->> users
             (map (fn [{:keys [id role]}]
                    [:tr {:key   id
                          :class (apply str role
                                        (when (= id (:id auth-user))
                                          [" " "is-me"]))}
                     [:td id]
                     [components/td-select {:selected role
                                            :options [{:value "admin" :label "admin"}
                                                      {:value "user"  :label "user"}]
                                            :on-change #(dispatch [:events/home.update-user-role {:id id
                                                                                                  :role %}])}]
                     ])))]]
      [:div.pt-2
       [:button.btn.btn-primary {:disabled (= file-resp :processing)
                                 :on-click #(dispatch [:events/home.export-users])}
        "Export"]]]
     [:div.pt-2
      (when-not (or (nil? file-resp)
                    (= file-resp :processing))
        [:a {:href (router/api-id->uri ::route/export-excel-get {:query-params file-resp})}
         "file.xlsx"])]]))