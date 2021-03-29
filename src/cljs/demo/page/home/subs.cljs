(ns demo.page.home.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :subs/home.users
  (fn [db _]
    (-> db :home :users)))

(reg-sub
  :subs/home.export-file-users
  (fn [db _]
    (-> db :home :export-file :users)))
