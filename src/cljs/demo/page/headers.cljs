(ns demo.page.headers
  (:require [re-frame.core :refer [subscribe dispatch]]))


(defn header-common []
  (let [sign-in? @(subscribe [:subs/user.signed-in?])]
    [:div.header.d-flex.justify-content-between
     [:div]
     [:div "Amazing re-frame demo!"]
     [:div [:a {:href "#" :on-click #(dispatch [:events/account.signout])}
            "Logout"]]]))


(defmulti header identity)

(defmethod header :default [_]
  [header-common])