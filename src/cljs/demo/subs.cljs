(ns demo.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as rf :refer [reg-sub subscribe]]
              [demo.page.home.subs]))

(reg-sub
 :subs/page.active-page
 (fn [db _]
   (-> db :active-page)))


(reg-sub
  :subs/user.signed-in?
  (fn [db _]
    (-> db :user-token some?)))


(reg-sub
  :subs/user.info
  (fn [db _]
    (:user-info db)))
