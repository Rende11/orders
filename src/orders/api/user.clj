(ns orders.api.user
  (:require [datomic.client.api :as d]
            [clojure.core.async :as a]))

(defn index [{:keys [conn] :as req}]
  (a/go
    (let [db     (d/db conn)
          users  (d/q
                  '[:find (pull ?id [:user/id :user/name :user/family])
                    :where
                    [?id :user/id]]
                  db)]
      {:status 200
       :body   (flatten users)})))


