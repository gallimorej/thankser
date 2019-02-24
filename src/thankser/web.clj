(ns thankser.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [thankser.core :as ty]
            [environ.core :refer [env]]
            [hiccup.core :refer [html]])
  (:gen-class))

; https://practicalli.github.io/clojure-webapps/introducing-ring/
; https://learnxinyminutes.com/docs/compojure/

(def DEFAULT-LANGUAGE :hawaiian)

(def SLACK-TEXT-KEY "text")

; Pulled from https://www.rosettacode.org/wiki/Strip_a_set_of_characters_from_a_string#Clojure
(defn strip [coll chars]
  (apply str (remove #((set chars) %) coll)))

(defn body []
      (html
        [:div
         [:h1 "Greetings!!!"]
         [:p "Hello from Thankser."]]))

(defn splash []
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (body)})

(defn handle-thanks-exception
  [e language]
  (if (str/starts-with? (.getMessage e) ty/LANGUAGE-NOT-FOUND)
    (str "I don't know how to say thank you in " (name language) ".")
    (str "Caught exception: " (.getMessage e))))

(defn get-help-page-body
  []
  (str "You invoked the following slash command: /ty\n"
       "To find out how to say thank you in a particular language, you need to identify the language using either the name of the language (like german) or the two-letter ISO 639 language code (like de).\n"
       "For example:\n"
       "/ty german\n"
       "/ty de\n"
       "You can also find out which languages Thankser knows by typing the following command:\n"
       "/ty ?"))

(defn get-languages-page-body
  []
  (str "Thankser knows how to say thank you in the following languages:\n"
       (strip (pr-str (map name (ty/get-languages))) "()\"")))

(defn get-thanks-page-body
  [slack-text]
  (case slack-text
    ("" nil "help") (get-help-page-body)
    "?" (get-languages-page-body)
    (let [slack-params (str/split slack-text #" ")
          language (keyword (first slack-params))]
      (try
        (str "{ \"response-type\": \"in_channel\", \"text\": \""
             (ty/get-thanks language)
             (if (> (count slack-params) 1)
               (str ", " (apply str (interpose " " (rest slack-params))) ""))
             "\" }")
        (catch Exception e (handle-thanks-exception e language))))))

(defn say-thanks-page [slack-text]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (get-thanks-page-body slack-text)})

(defn get-unknown-languages-page-body []
  (html
   [:div
    [:h1 "Unknown Languages"]
    [:ul (for [unknown-language (keys (deref ty/unknown-languages))]
           [:li (str (name unknown-language) " = " (@ty/unknown-languages unknown-language))])]]))

(defn show-unknown-languages-page []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (get-unknown-languages-page-body)})

(defroutes app
           (GET "/" []
                (splash))
           (GET "/say-thanks" {params :params}
                (say-thanks-page (params SLACK-TEXT-KEY)))
           (POST "/say-thanks" {params :params}
                (say-thanks-page (params SLACK-TEXT-KEY)))
           (GET "/show-unknown-languages" {}
                (show-unknown-languages-page))
           (ANY "*" []
                (route/not-found (slurp (io/resource "404.html")))))

(def handler
  (-> app
      (wrap-params)))
;(wrap-defaults site-defaults)))
;(site #'app))

(defn -main [& [port]]
      (let [port (Integer. (or port (env :port) 6000))]
        (jetty/run-jetty handler {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))