(ns doge-cloj.core-test
  (:require [clj-http.client :as c])
  (:use clojure.test
        [doge-cloj.core :as doge]
        [doge-cloj.config :as config]
        clj-http.fake))

(defn setup-test
  "setup and teardown of test environment"
  [f]
  ; your username and password
  (config/set-config {:username "rpcuser"
                      :password "rpcpassword"})
  (f))

(use-fixtures :once setup-test)

(deftest test-config
  (testing "Configuration"
    (is (= "localhost" (:host (config/config))))
    (is (= "22555" (:port (config/config))))))

(deftest test-getInfo
  (testing "getInfo"
    (let [ret (doge/getInfo)
          some-keys (map ret ["version" "proxy" "testnet"])]
      (is (> (count some-keys) 0)))))
