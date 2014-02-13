# doge-cloj

A Clojure library designed to connect to the DogeCoinD API.
Still pretty untested and lacking good unit tests.

This library also includes code that performs unit conversion from Dogecoin to Bitcoin and from Dogecoin to Dollar (via BTC -> Dollar). This can be found in currency.clj.

## Usage

Import it into your namespace

``` Clojure
(ns ...
    (:require
        [doge-cloj.core :as doge]
        [doge-cloj.config :as doge-config]
        ...
    ))
```

In order to work, the library needs to be initialized with the username and password 

``` Clojure
  (doge-config/set-config {:username "rpcuser"
                      :password "rpcpassword"})
```

Here, `rpcuser` and  `rpcpassword` are the dogecoind rpc login credentials that you defined in dogecoin.conf or that you set up on the commandline when starting dogecoind.

After this, you can just call the methods that are defined in core.clj

``` Clojure
(println (doge/getInfo))
```

## Unit Conversion

``` Clojure
(ns ...
  (:require [[doge-cloj.currency :as currency]]))

; Before first use, we have to update the converter with
; current exchange rates. This is only necessary once. However
; You should make sure to update this value every so many hours
; to reflect updated exchange rates.
(currency/update-values)

(println "1000 Doge are " (currency/conv-doge-dollar 1000) "dollar!")
```

## Unit Testing

If you want to unit test code that relies on calls from this library without starting and or stopping a dogecoind, you can bind `*mock-result*` to the value that you'd like to receive.

``` Clojure
(binding [ doge/*mock-result* {}]
      (let [results (core/call-that-relies-on-a-mock-result)]
        (is (= {} results))))
```

Warning: Currently, you can't mock on a per call basis, meaning if you need to unit test a function that first calls getInfo and then getTransaction you will get the same mock result for both. This will be changed soon.

## Installation

`doge-clj` is available as a Maven artifact from [Clojars](http://clojars.org/doge-cloj):

In your project.clj;

``` Clojure
:dependencies [[doge-cloj "0.1.0-SNAPSHOT"]
...]
```

## License

Copyright © 2014 Benedikt Terhechte

Distributed under the Eclipse Public License, the same as Clojure.
