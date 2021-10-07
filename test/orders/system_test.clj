(ns orders.system-test
  (:require [orders.system :as system]
            [integrant.core :as ig]
            [datomic.client.api :as d]))

(def user-1-id #uuid "11111111-1111-1111-1111-111111111111") 
(def user-2-id #uuid "22222222-2222-2222-2222-222222222222") 
(def user-3-id #uuid "33333333-3333-3333-3333-333333333333") 

(def initial-data
  [{:user/id user-1-id
    :user/name "Bruce"
    :user/family "Bayne"}
   {:user/id user-2-id
    :user/name "Luke"
    :user/family "Skyworker"}
   {:user/id user-3-id
    :user/name "Serge"
    :user/family "Gorelii"}])


(def testing-config
  (-> system/config
      (dissoc ::system/server)
      (assoc-in [::system/migrations :data] initial-data)
      (assoc-in [::system/db :db-conf] {:db-name "orders-test"
                                        :system "test"
                                        :storage-dir :mem
                                        :server-type :dev-local})))

(defn db-clean-up! [cfg]
  (let [db-conf (-> cfg ::system/db :db-conf)]
    (d/delete-database (d/client db-conf) db-conf)))

(def system (atom nil))

(defn start! []
  (reset! system (ig/init testing-config)))

(defn stop! []
  (when-let [sys @system]
    (db-clean-up! testing-config)
    (ig/halt! sys)
    (reset! system nil)))

(defn testing-env [t]
  (start!)
  (t)
  (stop!))

(defn get-app []
  (let [sys @system] 
    (:orders.system/handler (or sys (start!)))))

(comment
  @system

  (start!)

  (stop!)


  )

