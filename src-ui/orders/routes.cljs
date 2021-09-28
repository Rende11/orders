(ns orders.routes
  (:require [orders.pages.index.layout :as orders-index]
            [orders.pages.new.layout :as orders-new]))

(def routes
  {"/"   {:name :orders-index
          :view orders-index/view}
   "new" {:name :orders-new
          :view orders-new/view}})

