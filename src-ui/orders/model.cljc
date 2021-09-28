(ns orders.model
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [orders.pages.index.model :as orders]
            [orders.pages.index.layout :as orders-index]
            [orders.pages.new.layout :as orders-new]
))

(def routes
  {"" orders-index/view
   "index" orders-index/view
   "new" orders-new/view})

(rf/reg-cofx
 ::location
 (fn [cofx _]
   (assoc cofx :location (let [l (.-location js/document)]
                           (js/console.log l)
                           {:href (.-href l)
                            :host (.-host l)
                            :hash (str/replace (.-hash l) "#" "")}))))
(rf/reg-event-fx
 ::initialize
 [(rf/inject-cofx ::location)]
 (fn [{db :db loc :location} _]
   (prn "Dispatch")
   {:dispatch [::orders/load-orders]
    :db (assoc db :location loc)}))

(rf/reg-sub
 ::current-page
 (fn [db _]
   (let [hash (get-in db [:location :hash])
         page (get routes hash [:div.error "Page not found"])]
     page)))


