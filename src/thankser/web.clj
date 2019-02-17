(ns thankser.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.route :as route]
            [clojure.java.io :as io]
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

(def LANGUAGE-KEY "text")

(defn body []
      (html
        [:div
         [:h1 "Greetings!!!"]
         [:p "Hello from Thankser."]]))

(defn splash []
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (body)})

(defn say-thanks-page [language]
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body (ty/get-thanks language)})

(defroutes app
           (GET "/" []
                (splash))
           (GET "/say-thanks-param" {params :params}
                (println params)
                (println (params LANGUAGE-KEY))
                (say-thanks-page (keyword (params LANGUAGE-KEY))))
           (POST "/say-thanks-param" {params :params}
                 (println (params LANGUAGE-KEY))
                 (say-thanks-page (keyword (params LANGUAGE-KEY))))
           (GET "/say-thanks" []
                (say-thanks-page DEFAULT-LANGUAGE))
           (POST "/say-thanks" []
             (say-thanks-page DEFAULT-LANGUAGE))
           (GET "/test"
             {params :params} (prn "params:" params))
           (route/resources "/resources")
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