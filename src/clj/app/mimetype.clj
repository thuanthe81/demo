(ns app.mimetype
  (:require [ring.util.mime-type :as mimetype]))

(def ext-mimetypes
  {"xlsx" "application/vnd.openxml;formats-officedocument.spreadsheetml.sheet"})

(defn mimetype-of
  [filename]
  (mimetype/ext-mime-type filename ext-mimetypes))
