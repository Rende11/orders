(ns orders.pages.index.model
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]))

(rf/reg-event-fx
 ::load-orders
 (fn [{db :db} _]
   {:db   (assoc db :state :loading)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:9999/api/orders"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::load-orders-success]
                 :on-failure      [::load-orders-fail]}}))


(rf/reg-event-fx
 ::load-orders-success
 (fn [{db :db} [_ result]]
   {:db (assoc db :resp result)}))

(rf/reg-event-fx
 ::load-orders-fail
 (fn [{db :db} [_ result]]
   {:db (assoc db :error result)}))
