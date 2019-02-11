(ns thankser.core
  (:require [thankser.mongodb :as mongodb])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (do ((mongodb/get-documents!)

       (println "Hello, World!"))))

