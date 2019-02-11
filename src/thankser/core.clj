(ns thankser.core
  (:require [thankser.mongodb :as mongodb]
            [clojure.data.json :as json])
  (:gen-class))

;TODO: Load the map of thankses once

(defn read-thankses!
  "Reads in the thankses from the default JSON file or specified mongodb"
  ([]
   (json/read-str (slurp "data/thankses.json") :key-fn keyword))
  ([db]
   (let [msgs (mongodb/get-documents!)]
     (into {} msgs))))

(defn get-thanks
  "Get the appropriate thanks based on the language"
  [language]
  ;TODO: Handle the case where the language isn't in the map
  ;(language (read-thankses! mongodb/db)))
  (language (read-thankses!)))

(defn say-thanks
  ""
  [language]
  (str "Thanks in " language " is " (get-thanks language)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

