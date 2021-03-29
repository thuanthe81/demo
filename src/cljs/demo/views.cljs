(ns demo.views
  (:require [demo.utils :as utils]
            [goog.dom.classes]
            [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch subscribe]]
            [applied-science.js-interop :as j]
            [demo.page.headers :as headers]
            [demo.page.footers :as footers]
            [demo.page.home :refer [home-page]]
            [demo.page.sign-in :refer [sign-in-page]])
  (:import goog.Uri))

(defmulti render-page identity)

(defmethod render-page :page.home
  [_]
  [home-page])

(defmethod render-page :page.sign-in
  [_]
  [sign-in-page])

(defmethod render-page :default
  [_]
  (js/setTimeout #(dispatch [:events/demo.navigate-to :page.home]) 500)
  [:div "Page not found"])


(defn main-panel []
  (let [page @(subscribe [:subs/page.active-page])]
    [:div.container-fluid
     [headers/header page]
     [render-page page]
     [footers/footer page]]))
