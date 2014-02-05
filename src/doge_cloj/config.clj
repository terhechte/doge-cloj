(ns doge-cloj.config
  (:import (java.io File)))

(declare config)

(def defaults {:username "" :password "" :host "localhost" :port "22555" :scheme "http"})

(defn url-for []
  (format "%s://%s:%s" (:scheme (config)) (:host (config)) (:port (config))))

(def config-data (atom defaults))

(defn set-config
  "Set config with a map with the following keys {:username :password :host :port :scheme}
   only username and password are required, the rest defaults to http://localhost:22555"
  [new-config]
  (reset! config-data (merge defaults new-config)))

(defn config
  "Get the current config dictionary"
  []
  @config-data)

