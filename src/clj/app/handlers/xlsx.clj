(ns app.handlers.xlsx
  (:require [clj-xlsx.core :as xlsx]
            [app.config :refer [from-conf]]
            [app.mimetype :refer [mimetype-of]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [ring.util.response :as resp])
  (:import [java.util UUID]))

(defn base-dir
  []
  (-> (System/getProperty "java.io.tmpdir")
      (str "/" (str/replace (from-conf [:app-name]) #"[\s]+" "-"))))

(defn ->file-path
  [uuid]
  (str (base-dir) "/"
       uuid "/"
       "file.xlsx"))

(defn handler-export
  "Handler to export excel file. Response file exported."
  [{{sheets :body} :parameters}]
  (try (let [uuid (UUID/randomUUID)
             file-path (->file-path uuid)]
         (io/make-parents file-path)
         (->> sheets
              xlsx/->excel-byte-array
              (xlsx/write-file file-path))
         {:status 200
          :body {:id uuid}})
       (catch Exception ex
         {:status 500
          :body   (ex-data ex)})))

(defn handler-get-exported-file
  [{{{:keys [id]} :query} :parameters}]
  (let [path     (->file-path id)
        mimetype (mimetype-of path)]
    (if (.exists (io/file path))
      (-> path
          resp/file-response
          (assoc-in [:headers "Content-Type"] mimetype))
      {:status 404
       :body {:not-found "Not found your file."}})))