(ns doge-cloj.currency
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]
            [cheshire.parse :as parse]
            [clojure.pprint :as pprint]))

;; Simple functions to convert the price of dogecoins to the price of bitcoins
;; by converting them over btc
;; Offers an auto-update in a seperate thread that keeps the values up to date during the application
;; runtime

; Currently, update once every two hours
(def ^:dynamic *update-interval* (* 1000 60 120))

; we locally store the translaction values
(def btc-doge-value (atom 0))
(def btc-dollar-value (atom 0))

(def btc-doge-url "https://api.vircurex.com/api/get_highest_bid.json?base=DOGE&alt=BTC")
(def btc-dollar-url "http://api.bitcoincharts.com/v1/weighted_prices.json")

(def ^:dynamic *response-error* nil)

(defn- extract-value [url the-atom extract-fn]
  (binding [parse/*use-bigdecimals?* true]
    (let [result (client/get url)
          *response-error* (:error result)
          body (:body result)
          value (extract-fn (decode body))]
      (if *response-error*
        nil
        (reset! the-atom value)))))

(defn- update-values
  []
  (binding [*response-error* nil]
    (if-not (extract-value btc-doge-url btc-doge-value (fn [x](Double/parseDouble (get x "value"))))
      (println "Could not extract value from " btc-doge-url ":" *response-error*))) 
  (binding [*response-error* nil]
    (if-not (extract-value btc-dollar-url btc-dollar-value (fn [{:strs [USD]}](Double/parseDouble (get USD "24h"))))
      (println "Could not extract value from " btc-doge-url ":" *response-error*))))

(defn- print-values
  []
  (println "doge" @btc-doge-value "dollar" @btc-dollar-value))

(defn conv-doge-btc
  [value]
  (* value @btc-doge-value))

(defn conv-doge-dollar
  [value]
  (* @btc-dollar-value (conv-doge-btc value)))

(defn update-values-loop
  "load the current btc and doge values right after startup"
  []
  ; we want to do this on a seperate thread, and update from time to time
  (future
    (loop []
      (update-values)
      (Thread/sleep *update-interval*)
      (recur))))
