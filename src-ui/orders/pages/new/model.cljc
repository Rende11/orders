(ns orders.pages.new.model
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(defn ->json [data]
  (js/JSON.stringify (clj->js data)))

(rf/reg-event-fx
 ::field-update
 (fn [{db :db} [_ path value]]
   {:db (assoc-in db (concat [:order-new-page :form] path) value)}))

(rf/reg-sub
 ::users
 (fn [db _]
   (get-in db [:users])))

(rf/reg-sub
 ::page
 (fn [{db :db} _]
   (get-in db [:order-new-page])))

(rf/reg-sub
 ::field-value
 (fn [db [_ path]]
   (get-in db (concat [:order-new-page :form] path))))

(rf/reg-event-fx
 ::submit
 (fn [{db :db} _]
   (prn (get-in db [:order-new-page :form]))
   (let [order-form (get-in db [:order-new-page :form])]
     {:http-xhrio {:method :post
                   :uri "http://localhost:9999/api/orders"
                   :headers {"Content-Type" "application/json"}
                   :body (->json order-form)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success [::success-http-result]
                   :on-failure [::error-http-result]}})))
