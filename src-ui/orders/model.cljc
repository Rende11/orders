(ns orders.model
  (:require [re-frame.core :as rf]
            [orders.pages.index.model :as orders]))

(rf/reg-event-fx
 ::initialize
 (fn [{db :db} _]
   (prn "Dispatch")
   {:dispatch [::orders/load-orders]}))



