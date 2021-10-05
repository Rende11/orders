(ns orders.pages.new.model
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [clojure.string :as str]
            [reitit.frontend.easy :as rfe]))

(defn ->json [data]
  (js/JSON.stringify (clj->js data)))

(defn user-display [{:user/keys [name family]}]
  (str/join " " [name family]))


(rf/reg-event-fx
 ::load-users
 (fn [{db :db} _]
   {:db   (-> db (assoc :state :loading))
    :http-xhrio {:method          :get
                 :uri             "http://localhost:9999/api/users"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::load-users-success]
                 :on-failure      [::load-users-fail]}}))

(defn xf-users [users]
  (map (fn [u]
         (assoc u :user/display (user-display u))) users))

(rf/reg-event-fx
 ::load-users-success
 (fn [{db :db} [_ users]]
   {:db (assoc db :users (xf-users users))}))

(rf/reg-event-fx
 ::load-users-fail
 (fn [{db :db} [_ result]]
   {:db (assoc db :error result)}))

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
   (let [order-form (get-in db [:order-new-page :form])]
     {:http-xhrio {:method :post
                   :uri "http://localhost:9999/api/orders"
                   :headers {"Content-Type" "application/json"}
                   :body (->json order-form)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success [::success-submit]
                   :on-failure [::error-submit]}})))

(rf/reg-fx
 ::push-state
 (fn [v]
   (rfe/push-state v)))


(rf/reg-event-fx
 ::success-submit
 (fn [{db :db} _]
   {::push-state :orders-index
    :db (assoc-in db [:order-new-page :form] nil)}))

(rf/reg-event-fx
 ::error-submit
 (fn [{db :db} [_ result]]
   {:db (assoc db :error result)}))
