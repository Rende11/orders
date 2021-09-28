(ns orders.system
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [datomic.client.api :as d]
            [orders.web :as web]))

(def initial-data [{:db/id "bruceid"
                    :user/name "Bruce"
                    :user/family "Bayne"}
                   {:db/id "lukeid"
                    :user/name "Luke"
                    :user/family "Skyworker"}
                   {:user/name "Serge"
                    :user/family "Gorelii"}

                   {:order/title "Hello!"
                    :order/desc "Feature desc"
                    :order/author "bruceid"
                    :order/performer "lukeid"}
                   {:order/title "Fix bug"
                    :order/desc "Submit button on the index page doesn't works"
                    :order/author "lukeid"
                    :order/performer "bruceid"}
                   {:order/title "One more!"
                    :order/desc "One more time..."}])


(def config
  {::db {:db-conf {:server-type :dev-local
                   :system      "dev"
                   :db-name     "orders"}
         
         :schema [{:db/ident       :order/title
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "The title of the order"}
                  {:db/ident       :order/desc
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "The description of the order"}
                  {:db/ident       :order/author
                   :db/valueType   :db.type/ref
                   :db/cardinality :db.cardinality/one
                   :db/doc         "Author of the order"}
                  {:db/ident       :order/performer
                   :db/valueType   :db.type/ref
                   :db/cardinality :db.cardinality/one
                   :db/doc         "Implementer of the order"}
                  {:db/ident       :order/due-date
                   :db/valueType   :db.type/instant
                   :db/cardinality :db.cardinality/one
                   :db/doc         "Date of the order"}

                  {:db/ident       :user/name
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "User name"}
                  {:db/ident       :user/family
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "User family name"}]}

   ::server  {:port 9999, :handler (ig/ref ::handler)}
   ::handler {:db (ig/ref ::db)}})


(defmethod ig/init-key ::server [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key ::handler [_ {:keys [db]}]
  (fn [req]
    (web/app (assoc req :conn db))))

(defmethod ig/init-key ::db [_ {:keys [db-conf schema]}]
  (let [client (d/client db-conf)
        _ (d/create-database client db-conf)
        conn (d/connect client db-conf)]
    (d/transact conn {:tx-data schema})
    (d/transact conn {:tx-data initial-data})
    conn))


(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(comment
  (def system
    (ig/init config))

  (ig/halt! system)


  ((:orders.web/handler system) {:uri "/api/orders"
                                 :request-method :get
                                 :headers {"accept" "application/json"}})
(d/delete-database (d/client {:server-type :dev-local
                              :system "dev"}) {:db-name "orders"})

  )
