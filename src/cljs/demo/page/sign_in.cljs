(ns demo.page.sign-in
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))


(defn form-login
  []
  (let [state (r/atom nil)]
    (fn [props]
      (let [{:keys [username password]} (merge @state
                                               props)]
        [:div.container.login
         [:div.row.title "Login"]
         [:div.row
          [:div.col-sm-3 "Username:"]
          [:div.col.col-sm-9 [:input {:type     :text
                                      :on-change #(swap! state assoc :username (-> % .-target .-value))}]]
          [:div.w-100.p-2]
          [:div.col-sm-3 "Password:"]
          [:div.col.col-sm-9 [:input {:type :password
                                      :on-change #(swap! state assoc :password (-> % .-target .-value))}]]]
         [:div.p-2]
         [:div.row
          [:div.col-sm-12
           [:div.d-flex.flex-row-reverse
            [:button.btn.btn-primary {:on-click #(dispatch [:events/sign-in.submit (-> @state
                                                                                       (select-keys [:username :password]))])}
             "Login"]]]]]))))

(defn sign-in-page []
  [form-login])