(ns orders.api-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [orders.system-test :as st]
            [cheshire.core :as json]))

(use-fixtures :each st/testing-env)

(deftest test-index-ok
  (testing "index page is avaliable /testapp"
    (def index  ((st/get-app) {:request-method :get
                               :uri "/testapp"}))

    (is (= 200 (:status index)))
    (is (not-empty (slurp (:body index)))))

  (testing "redirect from root"
    (def index  ((st/get-app) {:request-method :get
                               :uri "/"}))

    (is (= 302 (:status index)))
    (is (= "/testapp" (get-in index [:headers "Location"])))))


(deftest test-orders-create-ok
  (testing "no orders by default"
    (def orders ((st/get-app) {:request-method :get
                               :uri "/api/orders"}))

    (is (= 200 (:status orders)))
    (is (empty? (json/parse-string (:body orders) true))))

  (testing "create order"
    (def order ((st/get-app) {:request-method :post
                              :uri "/api/orders"
                              :body {:title "Test"
                                     :desc "Test description"
                                     :author {:id (str st/user-1-id)},
                                     :performer {:id (str st/user-2-id)},
                                     :due-date "2021-10-04"}}))

    (is (= 201 (:status order)))
    (is (= {:order/title "Test"
            :order/desc "Test description"
            :order/author {:user/id (str st/user-1-id)}
            :order/performer {:user/id (str st/user-2-id)}
            :order/due-date "2021-10-04T09:00:00Z"}
           (-> (json/parse-string (:body order) true)
               (dissoc :order/id)))))

  (testing "orders list with created order"
    (def orders ((st/get-app) {:request-method :get
                               :uri "/api/orders"}))

    (is (= 200 (:status orders)))
    
    (is (= {:order/title "Test"
            :order/desc "Test description"
            :order/author {:user/id (str st/user-1-id)
                           :user/name "Bruce"
                           :user/family "Bayne"}
            :order/performer {:user/id (str st/user-2-id)
                              :user/name "Luke"
                              :user/family "Skyworker"}
            :order/due-date "2021-10-04T09:00:00Z"}

           (-> (json/parse-string (:body orders) true)
               first
               (dissoc :order/id))))))

(comment
  (clojure.test/run-tests)
  )
