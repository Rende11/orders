(ns orders.api.order
  (:require [datomic.client.api :as d]))



(defn index [{:keys [conn] :as req}]
  (let [db (d/db conn)
        orders (d/q
                    '[:find ?id ?title ?desc ?author-name ?author-family ?performer-name ?performer-family
                      :keys order/id order/title order/desc author/name author/family perf/name perf/family
                      :where
                      [?id :order/title ?title]
                      [?id :order/desc ?desc]
                      [?id :order/author ?aid]
                      [?id :order/performer ?pid]

                      [?aid :user/name ?author-name]
                      [?aid :user/family ?author-family]

                      [?pid :user/name ?performer-name]
                      [?pid :user/family ?performer-family]]
                    db)]
    {:status 200
     :body orders}))


(defn new [req]
  {:status 200
   :body "Form for create"})

(defn create [{:keys [conn body] :as req}]
  (def r req)
  (let [db (d/db conn)]
    {:status 201
     :body "Created"}))




(comment

  (def client (d/client {:server-type :dev-local
                       :system "dev"}))
  (d/create-database client {:db-name "movies"})

  (def conn (d/connect client {:db-name "orders"}))

  (d/transact )





  
  (d/q
                    '[:find ?title ?desc
                      ?author-name ?author-family
                      ?performer-name ?performer-family
                      :where
                      [?id :order/title ?title]
                      [?id :order/desc ?desc]
                      [?id :order/author ?aid]
                      [?id :order/performer ?pid]

                      [?aid :user/name ?author-name]
                      [?aid :user/family ?author-family]

                      [?pid :user/name ?performer-name]
                      [?pid :user/family ?performer-family]]
                    (d/db conn))



  (def movie-schema [{:db/ident :movie/title
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The title of the movie"}

                     {:db/ident :movie/genre
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/many
                      :db/doc "The genre of the movie"}

                     {:db/ident :movie/release-year
                      :db/valueType :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc "The year the movie was released in theaters"}])

  (d/transact conn {:tx-data movie-schema})

  (def first-movies [{:movie/title "The Goonies"
                      :movie/genre "action/adventure"
                      :movie/release-year 1985}
                     {:movie/title "Commando"
                      :movie/genre "thriller/action"
                      :movie/release-year 1985}
                     {:movie/title "Repo Man"
                      :movie/genre "punk dystopia"
                      :movie/release-year 1984}])

  (d/transact conn {:tx-data first-movies})

  (d/transact conn {:tx-data [{:movie/title "Totoro"}]})

  (def db (d/db conn))

  (def all-titles-q '[:find ?movie-title 
                      :where [_ :movie/title ?movie-title]])

  (d/q all-titles-q db)


  (d/q
   '[:find ?id ?t
     :where [?id :movie/title ?t]]
   db)

  (def more-genres [{:movie/title "Commando"
                     :movie/genre "comedy"}])

  (d/transact conn {:tx-data more-genres})

  (def orders [{:order/title "Fix bug"
                :order/desc "Please someone fix the issue #111"}
               {:order/title "Develop new feature"
                :order/desc "We need to add profile page"}
               ])

  (d/transact conn {:tx-data orders})
  )
