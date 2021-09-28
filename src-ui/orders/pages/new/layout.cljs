(ns orders.pages.new.layout
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]))



(defn view []
  [:div#order-new
   [:form
    [:input {:type "text" :placeholder "title..."}]
    [:textarea {:placeholder "description..."}]
    [:select {:name "author..."}]
    [:select {:name "performer..."}]
    [:input {:type "date" :placeholder "due date"}]
    [:a {:href (rfe/href :orders-index)} "Cancel"]
    [:button "Save"]]])
