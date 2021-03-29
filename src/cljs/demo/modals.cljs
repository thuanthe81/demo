(ns demo.modals
  (:require [demo.panels.users.core :as users]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [reagent.core :as r]))


#_(defn present-modal []
  (let [modal @(subscribe [:subs/modals.active-modal])]
    (case modal
      ;;:edit-user   (users/edit-user)
      ;;:create-user (users/create-user)
      nil)))

