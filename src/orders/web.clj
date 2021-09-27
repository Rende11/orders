(ns orders.web
  (:require [ring.middleware.json :refer [wrap-json-response]]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :as route]
            [orders.api.order :as order]
            [clojure.java.io :as io]))


(defroutes app-routes
  (GET "/testapp" [] (slurp (io/resource "index.html")))
  (context "/api" []
    (GET "/orders" req (order/index req))
    (GET "/orders/new" req (order/new req))
    (POST "/orders" req (order/create req)))
  (route/not-found "<h1>Resource not found</h1>"))

(def app
  (-> app-routes
      wrap-json-response))


