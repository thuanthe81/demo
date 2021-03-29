(ns demo.utils
  (:require [demo.db :as db]
            [ajax.core :as ajax]))


(def app-info-json
  (-> db/app-info
      clj->js
      js/JSON.stringify))


(defn transform-headers [& [{:keys [user-token] :as headers}]]
  (-> headers
      (dissoc :user-token)
      (merge {:app-info app-info-json}
             (when user-token
               {:authorization (str "Bearer " user-token)}))))


(def common-http-xhrio
  {:format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})})