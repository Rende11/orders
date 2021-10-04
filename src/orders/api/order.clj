(ns orders.api.order
  (:require [datomic.client.api :as d]
            [clojure.set :as clj.set]
            [tick.core :as t]))


(defn index [{:keys [conn] :as req}]
  (let [db     (d/db conn)
        orders (d/q
                '[:find (pull ?id [:order/id :order/title :order/desc :order/due-date
                                   {:order/author    [:user/id :user/name :user/family]
                                    :order/performer [:user/id :user/name :user/family]}])
                  :where
                  [?id :order/id]]
                db)
        users  (d/q
               '[:find ?uuid ?name ?family
                 :keys user/id user/name user/family
                 :where
                 [?id :user/id ?uuid]
                 [?id :user/name ?name]
                 [?id :user/family ?family]]
               db)]
    {:status 200
     :body   {:orders (flatten orders)
              :users  users}}))

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
  (let [order (->order body)]
    (try
      (d/transact conn {:tx-data [order]})
      {:status 201
       :body order}
      (catch Exception e
        {:status 400
         :body {:error (.getMessage e)}}))))


(comment

  (def client (d/client {:server-type :dev-local
                       :system "dev"}))

  (def conn (d/connect client {:db-name "orders"}))

  (d/q
   '[:find (pull ?id [*]) 
     :where [?id :order/id _]]
   (d/db conn))

  (def smp
    {:order/title "Tteteteett",
     :order/desc "asdasdad",
     :order/author {:user/id #uuid "4d62d121-33d0-4688-a4d4-121316ae51bf"},
     :order/performer {:user/id #uuid "90d42373-bebd-4013-907b-955438c05d72"},
     :order/due-date #inst "2021-10-02T09:00:00.000-00:00",
     :order/id #uuid "cfe1bc5a-d8f1-48a1-a06e-af0a2662c355"})
  (def smp-2
    {:order/title "Go to supermarket",
     :order/desc "Go to supermarket",
     :order/author {:user/id #uuid "90d42373-bebd-4013-907b-955438c05d72"},
     :order/performer
     {:user/id #uuid "80d42373-bebd-4013-907b-955438c05d72"},
     :order/due-date #inst "2021-10-06T09:00:00.000-00:00",
     :order/id #uuid "9136cbd7-e830-430c-a9c5-cc1a5346ee40"})
   
  (:tx-data (d/transact conn {:tx-data [smp-2]}))







  



  )
