(ns doge-cloj.config
  (:import (java.io File)))

(declare config)

(defn url-for []
  (format "%s://%s:%s" (:scheme (config)) (:host (config)) (:port (config))))

(let [defaults {:username "" :password "" :host "localhost" :port "22555" :scheme "http"}]
  (def config
    (memoize
     #(try 
        (let [config (apply hash-map (read-string (slurp (File. "config.clj"))))]
          (merge defaults config))
        (catch Exception e (do 
                             (print "Configuration not found using defaults.")
                             defaults))))))
