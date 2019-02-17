(ns thankser.core
  (:require [clojure.data.json :as json])
  (:gen-class))

;Reads in the thankses from the default JSON file
(def thankses (json/read-str (slurp "data/thankses.json") :key-fn keyword))

(defn get-thanks
  "Get the appropriate thanks based on the language"
  [language]
  (let [thanks (language thankses)]
       (if thanks
         thanks
         (throw
           (ex-info (str "Language not found: " language) {"language" language})))))

(defn say-thanks
  ""
  [language]
  (str "Thanks in " language " is " (get-thanks language)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

