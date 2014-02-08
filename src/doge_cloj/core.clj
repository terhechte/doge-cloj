(ns doge-cloj.core
  (:require [doge-cloj.config :as config]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [cheshire.parse :as parse]))

; The DogeCoin API is similar to the Litecoin API (from which Dogecoin was forked)
; An API description can be found here:
; https://litecoin.info/Litecoin_API
; As it says on that page, the Litecoin API in turn is similar to the Bitcoin API
; So for detailed documentation on some of these calls you may want to
; go for the Bitcoin docs right away.

;; Accounts:
;Accounts are named with arbitrary strings; you may use any JSON string other than "*" (JSON strings are sent and returned as UTF-8 encoded Unicode).

; Dogecoin creates two accounts automatically: it implicitly creates a default account with the empty string as its name, and it explicitly creates an account named Your Address when a new wallet is created.

(def ^:dynamic *mock-result* nil)

(defmacro def-doge-rpc [name doc & rest]
  `(defn ~name ~doc ~@rest
     (create-call ~(clojure.string/lower-case (str name)) ~@rest)))

(defn- perform-call
  "perform a http json call"
  [username password jsondata]
  ; if a binding to mock-result exists, we return mock-result instead.
  ; this allows us to unit test code depending on doge-clj without having to
  ; have a dogecoind running
  (if *mock-result*
    *mock-result*
    (binding [parse/*use-bigdecimals?* true] ; we need big decimals for proper currency support
      (let [url (config/url-for)
            json (encode (merge jsondata {:jsonrpc "1.0" :id :doge-clj}))
            cd {:basic-auth [username password]
                :content-type :json
                :accept :json
                :body json}
            result (client/post url cd)]
        (if-let [err (:error result)]
          (throw err)
          (if (= 200 (:status result))
            (-> result :body decode (get "result"))
            (-> result :body decode (get "error"))))))))
  

(defn- get-username
  "get the username from the config"
  []
  (:username (config/config)))

(defn- get-password
  "get the password from the config"
  []
  (:password (config/config)))

(defn- create-call
  "create the call with method and params "
  [method params]
  (let [username (get-username)
        password (get-password)
        call-dat {:method method :params params}]
    (perform-call username password call-dat)))

; Utility methods

; from https://github.com/kenrestivo/pawnshop/blob/master/src/pawnshop/core.clj
(defn longify-amounts
  "Required for conversion of JSON values to valid 64-bit longs which
   dogecoin amounts must be stored in.
   From the documentation https://en.bitcoin.it/wiki/Proper_Money_Handling_%28JSON-RPC%29
   (int64_t)(value * 1e8 + (value < 0.0 ? -.5 : .5))"
  [value]
  (-> value
      (* 1e8)
      (+ (if (< value 0.0) -0.5 0.5))
      long))

(defn floatify-amounts
  "Convert long bitcoin amounts back to a float"
  [value]
  (/ value 1e8))

; Implementation of the actual API methods

(def-doge-rpc addMultiSigAddress
  "adds a multisig address,
   publicKeys: List<String>
   nRequired: int
   account: string"
  [nRequired publicKeys account])

(def-doge-rpc backupWallet
  "destination: String"
  [destination])

(def-doge-rpc dumpPrivKey
  "dogecoinaddress: String"
  [dogecoinaddress])

(def-doge-rpc encryptWallet
  "passphrase: String"
  [passphrase])

(def-doge-rpc getAccount
  "dogecoinaddress: String"
  [dogecoinaddress])

(def-doge-rpc getAccountAddress
  "getaccountaddress will return the same address until coins are received on that address; once coins have been received, it will generate and return a new address.
   account: String"
  [account])

(def-doge-rpc getAddressByAccount
  "Use the getaddressesbyaccount method to list all addresses associated with an account.
   String account"
  [account])

(def-doge-rpc createRawTransaction
   "List<Map>: transactions [{'txid':txid,'vout':n}, ...]
    Map: outputs {address: amount, ...}"
   [transactions outputs])

(def-doge-rpc decodeRawTransaction
   "String: hexString)"
   [hexString])

(def-doge-rpc getInfo
  ""
  [])

(def-doge-rpc getPeerInfo
  ""
  [])

(def-doge-rpc getBalance
   "String: account
    String: minconf '1'"
   [account minconf])

(def-doge-rpc getBlock
   "String: hash)"
   [hash])

(def-doge-rpc getBlock
  "" [])

(def-doge-rpc getBlockHash
   "String: index)"
   [index])

(def-doge-rpc getBlockTemplate
   "String: params)"
   [params])

(def-doge-rpc getConnectionCount
  "" [])

(def-doge-rpc getDifficulty
  "" [])

(def-doge-rpc getGenerate
  "" [])

(def-doge-rpc getHashesPerSec
  "" [])

(def-doge-rpc getMiningInfo
  "" [])

(def-doge-rpc getNetworkHashPs
   "String: blocks)"
   [blocks])

(def-doge-rpc getNewAddress
   "getnewaddress always generates and returns a new address.
    String: account)"
   [account])

(def-doge-rpc getRawMemPool
  "" [])

(def-doge-rpc getRawTransaction
   "String: txid
    String: verbose '0'"
   [txid verbose])

(def-doge-rpc getReceivedByAccount
   "String: account
    String: minconf '1'"
   [account minconf])

(def-doge-rpc getReceivedByAddress
   "String: dogecoinaddress
    String: minconf '1'"
   [dogecoinaddress minconf])

(def-doge-rpc getTransaction
   "String: txid)"
   [txid])

(def-doge-rpc getWork
   "String: data)"
   [data])

(def-doge-rpc getWorkEx
  "List<String>[data, coinbase]: dataAndCoinbase"
  [dataAndCoinbase])

(def-doge-rpc help
   "String: command)"
   [command])

(def-doge-rpc importPrivKey
   "String: dogecoinprivkey
    String: label)"
   [dogecoinprivkey label])

(def-doge-rpc keyPoolRefill
  "" [])

(def-doge-rpc listReceivedByAccount
   "String: minconf '1'
    String: includeEmpty 'false'"
   [minconf includeEmpty])

(def-doge-rpc listReceivedByAddress
   "String: minconf '1'
    String: includeEmpty 'false'"
   [minconf includeEmpty])

(def-doge-rpc listSinceBlock
   "String: blockHash
    String: targetConfirmations)"
   [blockHash targetConfirmations])

(def-doge-rpc listTransactions
   "String: account
    String: count '10'
    String: from '0')"
   [account count from])

(def-doge-rpc listUnspent
   "String: minconf '1'
    String: maxconf '999999'"
   [minconf maxconf])

(def-doge-rpc move
   "String: fromAccount
    String: toAccount
    String: amount
    String: minconf '1'
    String: comment '99999'"
   [fromAccount toAccount amount minconf comment])

(def-doge-rpc sendFrom
   "String: fromAccount
    String: toDogeCoinAddress
    String: amount
    String: minconf
    String: comment
    String: commentTo"
   [fromAccount toDogeCoinAddress amount minconf comment commentTo])

(def-doge-rpc sendMany
  "String: fromaccount,
   Vector: toBitcoinAddresses {address:amount,...}
   String: minconf
   String: comment"
  [fromaccount  toBitcoinAddresses minconf comment])

(def-doge-rpc sendRawTransation
   "String: hexString)"
   [hexString])

(def-doge-rpc sendToAddress
   "String: dogecoinaddress
    String: amount
    String: comment
    String: commentTo)"
   [dogecoinaddress amount comment commentTo])

(def-doge-rpc setAccount
   "setaccount changes the account associated with an existing address. Coins previously received on that address (if any) will be debited from the previous account's balance and credited to the address' new account. Note that doing so may make the previous account's balance negative.
    String: dogecoinaddress
    String: account)"
   [dogecoinaddress account])

(def-doge-rpc setGenerate
   "String: generate
    String: genproclimit)"
   [generate genproclimit])

(def-doge-rpc setGenerate
   "String: amount)"
   [amount])

(def-doge-rpc setTxFee
   "String: amount)"
   [amount])

(def-doge-rpc signMessage
   "String: dogecoinaddress
    String: message)"
   [dogecoinaddress message])

(def-doge-rpc signRawTransaction
   "String: transactions [{'txid':txid,'vout':n,'scriptPubKey':hex},...]
    String: keys <privatekey1>,...
    String: sigHashType) sighashtype='ALL']"
   [transactions keys sigHashType])

(def-doge-rpc stop
  "" [])

(def-doge-rpc validateAddress
   "String: dogecoinaddress)"
   [dogecoinaddress])

(def-doge-rpc verifyMessage
   "String: dogecoinaddress
    String: signature
    String: message)"
   [dogecoinaddress signature message])


