(ns com.share.route)

(def routes
  ["/api"
   ["/users"
    ["" ::users]
    ["/:username"
     [""  ::user]
     ["/login" ::user-login]
     ["/refresh-token" ::user-refresh-token]
     ["/role"  ::user-role]]]
   ["/export"
    ["/excel" ::export-excel]
    ["/file.xlsx"   ::export-excel-get]]])