(ns orders.pages.index.layout
  (:require [re-frame.core :as rf]
            [orders.pages.index.model :as m]))



(defn view []
  (let [orders (rf/subscribe [::m/page])]
    [:div#order-index
     [:button "Add order"]
     [:div.orders
      (doall
       (for [o @orders]
         [:div.order {:key (:order/id o)
                      :style {:border "1px solid black"
                              :margin "20px"}}
          [:div.title (:order/title o)]
          [:div.desc (:order/desc o)]
          [:div.author (:author/display o)]
          [:div.performer (:perf/display o)]]))]]))
