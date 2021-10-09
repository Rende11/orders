(ns orders.system
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [datomic.client.api :as d]
            [orders.web :as web]))

(defn uuid []
  (java.util.UUID/randomUUID))

(def initial-data [{:db/id "bruceid"
                    :user/id (uuid)
                    :user/name "Bruce"
                    :user/family "Bayne"}
                   {:db/id "lukeid"
                    :user/id (uuid)
                    :user/name "Luke"
                    :user/family "Skyworker"}
                   {:db/id "sergeid"
                    :user/id (uuid)
                    :user/name "Serge"
                    :user/family "Gorelii"}
                   {:db/id "mikeid"
                    :user/id (uuid)
                    :user/name "Mike"
                    :user/family "Thompson"}
                   {:db/id "gloriaid"
                    :user/id (uuid)
                    :user/name "Gloria"
                    :user/family "Johnes"}

                   {:order/id (uuid)
                    :order/title "Buy food for the office"
                    :order/desc "We don't have enough milk and we ran out of coffee and donuts"
                    :order/author "bruceid"
                    :order/performer "lukeid"
                    :order/due-date #inst "2021-10-10T10:10:00.000000Z"}

                   {:order/id (uuid)
                    :order/title "Add light/dark theme support"
                    :order/desc "There should be a switch to select the theme and color design"
                    :order/author "lukeid"
                    :order/performer "bruceid"
                    :order/due-date #inst "2021-10-12T13:10:00.000000Z"}

                   {:order/id (uuid)
                    :order/title "Start an advertising campaign"
                    :order/desc "We need advertising on all major websites and platforms"
                    :order/author "lukeid"
                    :order/performer "sergeid"
                    :order/due-date #inst "2021-10-14T13:10:00.000000Z"}

                   {:order/id (uuid)
                    :order/title "Order cafe for a corporate party"
                    :order/desc "A small cafe will ok"
                    :order/author "mikeid"
                    :order/performer "gloriaid"
                    :order/due-date #inst "2021-10-15T13:10:00.000000Z"}])
(def config
  {::db {:db-conf {:server-type :dev-local
                   :storage-dir :mem
                   :system      "dev"
                   :db-name     "orders"}
         
         :schema [{:db/ident       :order/id
                   :db/valueType   :db.type/uuid
                   :db/cardinality :db.cardinality/one
                   :db/unique      :db.unique/identity
                   :db/doc         "The id of order"}
                  {:db/ident       :order/title
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

                  {:db/ident       :user/id
                   :db/valueType   :db.type/uuid
                   :db/cardinality :db.cardinality/one
                   :db/unique      :db.unique/identity
                   :db/doc         "The id of order"}
                  {:db/ident       :user/name
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "User name"}
                  {:db/ident       :user/family
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "User family name"}]}

   ::migrations {:db (ig/ref ::db) :data initial-data}

   ::server  {:port 9999, :handler (ig/ref ::handler)}
   ::handler {:db (ig/ref ::db)}})


(defmethod ig/init-key ::server [_ {:keys [handler port]}]
  (prn (str "Server is launching on port: " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key ::handler [_ {:keys [db]}]
  (fn [req]
    (web/app (assoc req :conn db))))


(defmethod ig/init-key ::db [_ {:keys [db-conf schema]}]
  (let [client (d/client db-conf)
        _ (d/create-database client db-conf)
        conn (d/connect client db-conf)]
    (d/transact conn {:tx-data schema})
    conn))

(defn already-has-data? [conn]
  (when-let [count (ffirst (d/q
                            '[:find (count ?id)
                              :where [?id :order/id]]
                            (d/db conn)))]
    (pos? count)))

(defmethod ig/init-key ::migrations [_ {conn :db data :data}]
  (when-not (already-has-data? conn)
    (d/transact conn {:tx-data data})))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(defn start []
  (ig/init config))

(defn stop [sys]
  (ig/halt! sys))

(comment
  (def system
    (start))

  (stop system)

  (d/delete-database (d/client {:server-type :dev-local
                                :storage-dir :mem
                                :system "dev"}) {:db-name "orders"})
  
  
  ((:orders.system/handler system) {:uri "/api/orders"
                                    :request-method :get
                                    :headers {"accept" "application/json"}})

  )
