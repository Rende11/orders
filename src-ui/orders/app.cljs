(ns orders.app
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [orders.model :as m]
            ))


(defn app []
  (let [page (rf/subscribe [::m/current-page])]
    [:div
     [@page]]))


(defn ^:dev/after-load render []
  (rdom/render [app] (.getElementById js/document "root")))


(defn ^:export init
  []
  (rf/dispatch-sync [::m/initialize])     
  (render))
