(ns orders.core
  (:require [orders.system :as system])
  (:gen-class))


(defn -main [& args]
  (system/start))
