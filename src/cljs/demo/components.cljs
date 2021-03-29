(ns demo.components
  (:require [reagent.core :as r]))


(defn td-select
  [state]
  (let [state (r/atom state)]
    (fn [props]
      (let [{:keys [selected
                    on-change
                    options]} (merge @state
                                     props)]
        [:td
         [:div.td-select-container
          [:select {:value     selected
                    :on-change #(when on-change
                                  (on-change (-> % .-target .-value)))}
           (->> options
                (map (fn [opt]
                       [:option {:key      (:value opt)
                                 :value    (:value opt)
                                 ;;:selected (= selected (:value opt))
                                 }
                        (:label opt)])))]]]
        ))))