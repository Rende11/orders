(ns orders.web
  (:require [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.util.response :refer [resource-response redirect]]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.response]
            [ring.core.protocols] 
            [compojure.route :as route]
            [orders.api.order :as order]
            [orders.api.user :as user]
            [clojure.core.async :as a]))



(defroutes app-routes
  (GET "/" [] (redirect "/testapp"))
  (GET "/testapp" [] (resource-response "public/index.html"))
  (context "/api" []
    (GET "/orders" req (order/index req))
    (POST "/orders" req (order/create req))
    (GET "/users" req (user/index req)))
  (route/resources "/")       
  (route/not-found "<h1>Resource not found</h1>"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-json-body {:keywords? true})
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])))

(extend-protocol compojure.response/Renderable
  clojure.core.async.impl.channels.ManyToManyChannel
  (render [ch req]
    (a/<!! ch)))


