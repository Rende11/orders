(ns orders.web
  (:require [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [resource-response]]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :as route]
            [orders.api.order :as order]))


(defroutes app-routes
  (GET "/" [] (resource-response "index.html"))
  (GET "/testapp" [] (resource-response "index.html"))
  (context "/api" []
    (GET "/orders" req (order/index req))
    (POST "/orders" req (order/create req)))
  (route/files "/")
  (route/resources "/")       
  (route/not-found "<h1>Resource not found</h1>"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-json-body {:keywords? true})))




