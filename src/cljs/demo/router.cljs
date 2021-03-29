(ns demo.router
  (:require [cljs.core.async :refer [>! <! chan close!] :refer-macros [go]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [reitit.frontend :as rf]
            [reitit.core :as rc]
            [com.share.route :as route]))


(def api-router
  {(str 'com.share.route) (rf/router route/routes)})

(defn api-id->uri
  ([api-id]
   (api-id->uri api-id nil))
  ([api-id {:keys [path-params
                   query-params]}]
   (some-> api-id
           (namespace)
           (api-router)
           (rc/match-by-name! api-id path-params)
           (rc/match->path query-params))))

(def page-router
  (rf/router [["/"
               {:name   :page.home
                :events [[:events/home.get-all-users]]}]
              ["/sign-in"
               {:name :page.sign-in}]]))


(defn page-uri->page-id [uri]
  (-> (rf/match-by-path page-router uri)
      (get-in [:data :name])))


(defn page-id->settings
  [page-id]
  (when-let [page (rf/match-by-name page-router page-id)]
    {:path   (:path page)
     :events (get-in page [:data :events])}))

(defroute home "/" []
  (re-frame/dispatch [:events/demo.navigate-to :page.home]))


(defroute sign-in "/sign-in" []
  (re-frame/dispatch [:events/demo.navigate-to :pages.sign-in]))

(defn init! [])