(ns demo.page.footers)

(defn footer-common []
  [:div.footer
   [:div "Developer: BUI THE MINH THUAN!"]])


(defmulti footer identity)

(defmethod footer :default [_]
  [footer-common])