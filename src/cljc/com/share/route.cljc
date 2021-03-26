(ns com.share.route)

(def routes
  ["/api"
   ["/users"
    ["" ::users]
    ["/:username"
     [""  ::user]
     ["/login" ::user-login]
     ["/role"  ::user-role]]]
   ["/export"
    ["/excel" ::export-excel]
    ["/file.xlsx"   ::export-excel-get]]])