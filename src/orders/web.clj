(ns orders.web
  (:require [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [resource-response redirect]]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.response]
            [ring.util.response] 
            [ring.core.protocols] 
            [compojure.route :as route]
            [orders.api.order :as order]
            [orders.api.user :as user]
            [clojure.core.async :as a]
            [clojure.java.io :as io]))



(defroutes app-routes
  (GET "/" [] (redirect "/testapp"))
  (GET "/testapp" [] (resource-response "index.html"))
  (context "/api" []
    (GET "/orders" req (order/index req))
    (POST "/orders" req (order/create req))
    (GET "/users" req (user/index req)))
  (route/files "/")
  (route/resources "/")       
  (route/not-found "<h1>Resource not found</h1>"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-json-body {:keywords? true})))

(extend-protocol compojure.response/Renderable
  clojure.core.async.impl.channels.ManyToManyChannel
  (render [ch req]
    (a/<!! ch)))





