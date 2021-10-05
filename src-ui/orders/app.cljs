(ns orders.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [orders.routes :as rt]
            [reitit.frontend :as rfr]
            [reitit.frontend.easy :as rfe]))


(defonce match (r/atom nil))

(defn app []
  (let [page (-> @match :data :view)]
    [:div#app
     [page]]))


(defn ^:dev/after-load render []
  (rdom/render [app] (.getElementById js/document "root")))


(defn ^:export init
  []
  (rfe/start!
   (rfr/router rt/routes)
   (fn [m]
     (reset! match m)
     (when-let [evt (-> m :data :event)]
       (rf/dispatch [evt])))
   {:use-fragment true})
  (render))
