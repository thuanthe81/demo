(ns app.server
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [muuntaja.core :as mu]
            [reitit.ring.coercion :as coercion]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [mount.core :as m]
            [app.api]
            [com.share.route :as route]
            [app.config :refer [from-conf]]
            [clojure.string :as str]
            [reitit.core :as r])
  (:import (org.eclipse.jetty.server Server)))


(defn swagger-base
  []
  (str "/"
       (->> (from-conf [:app-name])
            ((fn [s] (str/replace s #"[^\w]+" "-")))
            str/lower-case)
       "-swagger"))


(defn route-expander
  [registry]
  (fn [data opts]
    (if (keyword? data)
      (some-> data
              registry
              (r/expand opts)
              (assoc :name data))
      (r/expand data opts))))


(defn handlers
  []
  (ring/ring-handler
    (ring/router
      [route/routes
       [(str (swagger-base) "/swagger.json") :swagger]]
      {:expand   (route-expander (merge app.api/route-handler-specs
                                        {:swagger {:get {:no-doc  true
                                                         :swagger {:info {:title "Questions, Levels(Templates), Preset-tags API"}}
                                                         :handler (swagger/create-swagger-handler)}}}))
       :data     {:coercion   reitit.coercion.spec/coercion
                  :muuntaja   mu/instance
                  :middleware [params/parameters-middleware
                               muuntaja/format-middleware
                               coercion/coerce-exceptions-middleware
                               coercion/coerce-request-middleware
                               coercion/coerce-response-middleware
                               ]}})
    (ring/routes
      (ring/create-resource-handler {:path "/" :root "public"})
      (swagger-ui/create-swagger-ui-handler {:path (swagger-base)
                                             :url  (str (swagger-base) "/swagger.json")})
      (ring/create-default-handler))))


(defn- start-server []
  (let [port (from-conf [:port])
        s    (jetty/run-jetty (handlers)
                              {:port port, :join? false})]
    (println (from-conf [:app-name]) "is running on" port "with swagger path" (swagger-base))
    s))


(declare server)


(defn- stop-server []
  (when server
    (.stop ^Server server)))


(m/defstate server
  :start (start-server)
  :stop  (stop-server))