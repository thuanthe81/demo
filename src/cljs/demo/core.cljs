(ns demo.core
    (:require [reagent.core :as reagent]
              [reagent.dom :as rd]
              [re-frame.core :as rf]
              [demo.events]
              [demo.subs]
              [demo.router :as router]
              [demo.views :as views]))


(defn mount-root []
  (rf/clear-subscription-cache!)
  (rd/unmount-component-at-node (.getElementById js/document "app"))
  (rd/render [views/main-panel]
             (.getElementById js/document "app")))

(defn ^:export init []
  (router/init!)
  (rf/dispatch-sync [:events/demo.initialize-db])
  (mount-root))
