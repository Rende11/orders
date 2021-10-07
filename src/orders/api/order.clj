(ns orders.api.order
  (:require [datomic.client.api :as d]
            [clojure.set :as clj.set]
            [tick.core :as t]
            [clojure.core.async :as a]))


(defn index [{:keys [conn] :as req}]
  (a/go
    (let [db     (d/db conn)
          orders (d/q
                  '[:find (pull ?id [:order/id :order/title :order/desc :order/due-date
                                     {:order/author    [:user/id :user/name :user/family]
                                      :order/performer [:user/id :user/name :user/family]}])
                    :where
                    [?id :order/id]]
                  db)]
      {:status 200
       :body   (flatten orders)})))

(defn ->inst [date]
  (t/inst (t/at (t/date date) (t/time "12:00"))))

(defn ->uuid [s]
  (java.util.UUID/fromString s))

(defn ->order [raw-order]
  (-> (into {} (map (fn [[k v]] {(keyword "order" (name k)) v})) raw-order)
      (update :order/author #(clj.set/rename-keys % {:id :user/id}))
      (update :order/performer #(clj.set/rename-keys % {:id :user/id}))
      (update :order/due-date ->inst)
      (update-in [:order/author :user/id] ->uuid)
      (update-in [:order/performer :user/id] ->uuid)
      (assoc :order/id (java.util.UUID/randomUUID))))

(defn create [{:keys [conn body] :as req}]
  (a/go
    (let [order (->order body)]
      (try
        (d/transact conn {:tx-data [order]})
        {:status 201
         :body order}
        (catch Exception e
          {:status 400
           :body {:error (.getMessage e)}})))))


(comment
  (def client (d/client {:server-type :dev-local
                         :system "dev"}))

  (def conn (d/connect client {:db-name "orders"}))

  (d/q
   '[:find (count ?id) 
     :where [?id :order/id]]
   (d/db conn)))

  
  
   
  







  



