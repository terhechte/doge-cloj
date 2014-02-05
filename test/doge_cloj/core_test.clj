(ns doge-cloj.core-test
  (:require [clj-http.client :as c])
  (:use clojure.test
        [doge-cloj.core :as doge]
        [doge-cloj.config :as config]
        clj-http.fake))

; not working yet
(comment deftest test-running
  (testing "Test the getAccount call"
    (with-fake-routes
      {
       (config/url-for) (fn [r] {:status 200 :headers {} :body "this is a test"})
       }
      (is (= [] (doge/getInfo "test-wallet")))
      )))

(deftest test-config
  (testing "Configuration"
    (is (= "localhost" (:host (config/config))))
    (is (= "22555" (:port (config/config))))))

(deftest test-getInfo
  (testing "getInfo"
    (let [ret (doge/getInfo)
          some-keys (map ret ["version" "proxy" "testnet"])]
      (is (> (count some-keys) 0)))))
