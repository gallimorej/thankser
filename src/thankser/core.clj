(ns thankser.core
  (:require [clojure.data.json :as json]
            [thankser.mongodb :as mongodb])
  (:gen-class))

(def LANGUAGE-NOT-FOUND "Language not found")

(def SNARK :snark)

;Reads in the thankses from the default JSON file
(def thankses (json/read-str (slurp "data/thankses.json") :key-fn keyword))

;Initializes the unknown languages from the mongodb database
(def unknown-languages (atom (first (mongodb/get-documents!))))

;Handle two cases
;The unknown language isn't in the map, in which case add it with a call count of 1 (that is the "fnil" expression)
;The unknown language is in the map, in which case increment the call count
(defn log-unknown-language!
  [unknown-language]
  (do
    (swap! unknown-languages assoc unknown-language ((fnil inc 0) (@unknown-languages unknown-language)))
    (mongodb/update-unknown-languages (deref unknown-languages))))

(defn get-snark-thanks
  "Get a random snarky thanks"
  []
  (let [snarky-thankses (SNARK thankses)]
    ((nth (keys snarky-thankses) (rand-int (count snarky-thankses))) snarky-thankses)))

(defn get-thanks
  "Gets the appropriate thanks based on the language"
  [language]
  (if (= language SNARK)
    (get-snark-thanks)
    (let [thanks (language thankses)]
      (if thanks
        thanks
        (do
          (log-unknown-language! language)
          (throw
            (ex-info (str LANGUAGE-NOT-FOUND ": " language) {"language" language})))))))

(defn say-thanks
  "Gets the thank you corresponding to the specified langauge"
  [language]
  (str "Thanks in " language " is " (get-thanks language)))

(defn get-languages
  "Returns the languages that Thankser knows"
  []
  (sort (keys thankses)))

(defn get-unknown-languages
  "Returns the map of unknown languages and the count"
  []
  (rest (first (mongodb/get-documents!))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

