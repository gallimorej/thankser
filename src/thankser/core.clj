(ns thankser.core
  (:require [clojure.data.json :as json]
            [thankser.mongodb :as mongodb])
  (:gen-class))

(def language-not-found "Language not found")

;Reads in the thankses from the default JSON file
(def thankses (json/read-str (slurp "data/thankses.json") :key-fn keyword))

;Initializes the unknown languages from the mongodb database
(def unknown-languages (atom (first (mongodb/get-documents!))))

(def safe-inc (fnil inc 0))

;Handle two cases
;The unknown language isn't in the map, in which case add it with a call count of 1 (that is the "fnil" expression)
;The unknown language is in the map, in which case increment the call count
(defn log-unknown-language!
  [unknown-language]
  (swap! unknown-languages update unknown-language safe-inc)
  (mongodb/update-unknown-languages! (deref unknown-languages)))

;There's a code smell in here
(defn get-snark-thanks
  "Get a random snarky thanks"
  []
  (let [snarky-thankses (:snark thankses)]
    ((nth (keys snarky-thankses) (rand-int (count snarky-thankses))) snarky-thankses)))

(defn get-thanks
  "Gets the appropriate thanks based on the language"
  [language]
  (if (= language :snark)
    (get-snark-thanks)
    (let [thanks (language thankses)]
      (if thanks
        thanks
        (do
          (log-unknown-language! language)
          (throw
            (ex-info (str language-not-found ": " language) {"language" language})))))))

(defn get-languages
  "Returns the languages that Thankser knows"
  []
  (sort (keys thankses)))

(comment
    (defn say-thanks
      "Gets the thank you corresponding to the specified langauge"
      [language]
      (str "Thanks in " language " is " (get-thanks language)))


    (defn get-unknown-languages
      "Returns the map of unknown languages and the count"
      []
      (rest (first (mongodb/get-documents!))))

    (defn -main
      "I don't do a whole lot ... yet."
      [& args]
      (println "Hello, World!")))


