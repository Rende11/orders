(ns orders.pages.index.layout
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [orders.pages.index.model :as m]))



(defn view []
  (let [orders (rf/subscribe [::m/page])]
    [:div#order-index
     [:div.orders
      [:div.order-index-header
       [:h3.title "Orders"]
       [:a.add-href {:href (rfe/href :orders-new)} "add"]]
      (doall
       (for [o @orders]
         [:div.order {:key (:order/id o)}
          [:div.order-header
           [:div.order-title.order-info (:order/title o)]
           [:div.order-users
            [:div.order-due-date.order-info [:span.order-label "date:"]  (:order/due-date o)]
            [:div.order-author.order-info [:span.order-label "author:"] (:author/display o)]
            [:div.order-performer.order-info [:span.order-label "performer:"] (:perf/display o)]]]
          [:div.order-desc.order-info (:order/desc o)]]))]]))
