(ns thankser.core
  (:require [clojure.data.json :as json])
  (:gen-class))

(def LANGUAGE-NOT-FOUND "Language not found")

;Reads in the thankses from the default JSON file
(def thankses (json/read-str (slurp "data/thankses.json") :key-fn keyword))

(def unknown-languages (atom {}))

;Handle two cases
;The unknown language isn't in the map, in which case add it with a call count of 1 (that is the "fnil" expression)
;The unknown language is in the map, in which case increment the call count
(defn log-unknown-language!
  [unknown-language]
  (swap! unknown-languages assoc unknown-language ((fnil inc 0) (@unknown-languages unknown-language))))

(defn get-thanks
  "Gets the appropriate thanks based on the language"
  [language]
  (let [thanks (language thankses)]
       (if thanks
         thanks
         (do
           (log-unknown-language! language)
           (throw
             (ex-info (str LANGUAGE-NOT-FOUND ": " language) {"language" language}))))))


(defn say-thanks
  "Gets the thank you corresponding to the specified langauge"
  [language]
  (str "Thanks in " language " is " (get-thanks language)))

(defn get-languages
  "Returns the languages that Thankser knows"
  []
  (sort (keys thankses)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

