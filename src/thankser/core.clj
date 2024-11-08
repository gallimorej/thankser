(ns thankser.core
  (:require [clojure.data.json :as json])
  (:gen-class))

(def language-not-found "Language not found")

;Reads in the thankses from the default JSON file
(def thankses (json/read-str (slurp "data/thankses.json") :key-fn keyword))

(def unknown-languages-coll "unknown-languages")

;Initializes the unknown languages from the mongodb database
;(def unknown-languages (atom (first (mongodb/get-documents! unknown-languages-coll))))

(def safe-inc (fnil inc 0))

;Handle two cases
;The unknown language isn't in the map, in which case add it with a call count of 1 (that is the "safe-inc" expression)
;The unknown language is in the map, in which case increment the call count
;(defn log-unknown-language!
;  [unknown-language]
;  (swap! unknown-languages update unknown-language safe-inc)
;  (mongodb/update-unknown-languages! unknown-languages-coll @unknown-languages))

(defn get-one-thanks
  "Get one thanks"
  [x-or-xs]
  (rand-nth (if (coll? x-or-xs) x-or-xs [x-or-xs])))

; TODO move the log-unknown-languages out of this function to make it pure
(defn get-thanks!
  "Gets the appropriate thanks based on the language"
  [language thankses]
  (if-let [thanks (get-one-thanks (language thankses))]
    thanks
    (do
      ;(log-unknown-language! language)
      :language-not-found)))

(defn get-languages
  "Returns the languages that Thankser knows"
  []
  (sort (keys thankses)))

(comment
    (defn say-thanks
      "Gets the thank you corresponding to the specified langauge"
      [language]
      (str "Thanks in " language " is " (get-thanks language)))


    ;(defn get-unknown-languages
      ;"Returns the map of unknown languages and the count"
      ;[]
      ;(rest (first (mongodb/get-documents! unknown-languages-coll))))

    (defn -main
      "I don't do a whole lot ... yet."
      [& args]
      (println "Hello, World!"))

    (do
      ;(log-unknown-language! language)
      ;TODO don't throw an exception. instead, return an exception key -- like :not-found
      (throw
        (ex-info (str language-not-found ": " language) {"language" language}))))





