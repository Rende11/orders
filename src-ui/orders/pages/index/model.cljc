(ns orders.pages.index.model
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [clojure.string :as str]))


(rf/reg-event-fx
 ::load-orders
 (fn [{db :db} _]
   {:db   (assoc db :state :loading)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:9999/api/orders"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::load-orders-success]
                 :on-failure      [::load-orders-fail]}}))


(defn user-display [{:user/keys [name family]}]
  (str/join " " [name family]))

(defn date-display [due-date]
  (first (str/split (or due-date "") #"T")))

(defn xf-orders [orders]
  (sort-by :order/due-date >
           (map (fn [o]
                  (-> o
                      (assoc :author/display (user-display (:order/author o))
                             :perf/display (user-display (:order/performer o)))
                      (update :order/due-date date-display))) orders)))


(rf/reg-event-fx
 ::load-orders-success
 (fn [{db :db} [_ orders]]
   {:db (assoc db :orders (xf-orders orders))}))

(rf/reg-event-fx
 ::load-orders-fail
 (fn [{db :db} [_ result]]
   {:db (assoc db :error result)}))



(rf/reg-sub
 ::page
 (fn [db _]
   (get-in db [:orders])))
