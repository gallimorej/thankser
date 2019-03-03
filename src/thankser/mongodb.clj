(ns thankser.mongodb
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]])
  (:import [com.mongodb MongoOptions WriteResult ServerAddress]))

(def uri (format (env :mongo-db-url)
                 (env :mongo-db-user)
                 (env :mongo-db-pwd)
                 (env :mongo-db-name)))

(defonce db (atom nil))

(def UNKNOWN-LANGUAGES-COLL "unknown-languages")

; connection to monggodb
(reset! db
        (try
          (mg/connect-via-uri uri)
          (catch Exception e (str "caught exception: "
                                  (.getMessage e))
                             nil)))

(defn update-user-record!
  " given trello user name, update the board record in database
    returns number of records changed; should almost always be 1? "
  [username record]
  ;       [string? map? => int?]
  (let [retval (mc/update (:db @db)
                          {:name username}
                          {:name    username
                           :hotkeys record}
                          {:upsert true})
        ; number of records changed
        n      (.getN retval)]
    n))

(defn count-documents
  " count how many documents in the database "
  []
  ;      [=> int?]
  (mc/count (:db @db) UNKNOWN-LANGUAGES-COLL))

(defn delete-all-documents!
  " because of danger, first arg must be 'confirm' "
  [confirm-str collection]
  ;      [string? string? => boolean?]
  (if (= confirm-str "confirm")
    (do
      (mc/remove (:db @db) collection)
      true)
    false))

(defn load-documents!
  [collection]
  (let [{:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/insert-batch db UNKNOWN-LANGUAGES-COLL collection)))

;
; end
;

(defn get-documents!
  []
  (let [{:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/find-maps db UNKNOWN-LANGUAGES-COLL)))


(defn- testx [username record]
  ; returns
  ; {:_id #object[org.bson.types.ObjectId 0x24870ae1 "5b67dbc7e6136b967e082b85"], :name "123", :hotkeys {:a 1}}
  (mc/insert-and-return (:db @db)  "documents"
                        {:name username :hotkeys record})
  ;
  ; returns
  ; #object[com.mongodb.WriteResult 0x3371670b "WriteResult{, n=1, updateOfExisting=true, upsertedId=null}"]
  (let [r (mc/update (:db @db)  "documents"
                     {:name "xyz"}
                     {:name "xyz"
                      :hotkeys {:a 1}}
                     {:upsert true})
        n (.getN r)]))

(defn test-db
  " do a test transaction "
  []
  (let [{:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/insert-and-return db "documents" {:name "John" :age 30})))
