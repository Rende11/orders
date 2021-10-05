(ns orders.routes
  (:require [orders.pages.index.layout :as orders-index]
            [orders.pages.index.model :as index-model]
            [orders.pages.new.layout :as orders-new]
            [orders.pages.new.model :as new-model]))

(def routes
  {"/"   {:name :orders-index
          :event ::index-model/load-orders
          :view orders-index/view}
   "new" {:name :orders-new
          :event ::new-model/load-users
          :view orders-new/view}})

