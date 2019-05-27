(ns thankser.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [thankser.core :as ty]
            [thankser.mongodb :as tm]
            [environ.core :refer [env]]
            [hiccup.core :refer [html]])
  (:gen-class))

; https://practicalli.github.io/clojure-webapps/introducing-ring/
; https://learnxinyminutes.com/docs/compojure/

(def slack-text-key "text")

(def html-header {"Content-Type" "text/html"})
(def json-header {"Content-Type" "application/json"})

; Pulled from https://www.rosettacode.org/wiki/Strip_a_set_of_characters_from_a_string#Clojure
(defn strip [coll chars]
  (apply str (remove #((set chars) %) coll)))

(defn response
  [status headers body]
  {:status status
   :headers headers
   :body body})

(def ok (partial response 200))
(def bad-request (partial response 400))
(def not-found (partial response 404))

(def splash (ok html-header (html
                              [:div
                               [:h1 "Greetings!!!"]
                               [:p "Hello from Thankser."]])))

(defn handle-language-not-found
  [language]
  (str "I don't know how to say thank you in " (name language) "."))

(def help-page-body
  (str/join "\n" ["You invoked the following slash command: /ty"
                  "To find out how to say thank you in a particular language, you need to identify the language using either the name of the language (like german) or the two-letter ISO 639 language code (like de)."
                  "For example:"
                  "/ty german"
                  "/ty de"
                  "You can also find out which languages Thankser knows by typing the following command:"
                  "/ty ?"
                  "Finally, if you want to thank someone directly, you can @mention them following the language you want. You can @mention as many people as you'd like to thank."
                  "For example:"
                  "/ty german @SlackUser"]))

(defn get-languages-page-body
  []
  (str "Thankser knows how to say thank you in the following languages:\n"
       (strip (pr-str (map name (ty/get-languages))) "()\"")))

(defn get-thanks-page-body
  [slack-text thankses]
  (case slack-text
    ("" nil "help") {:response_type "ephemeral"
                     :text help-page-body}
    "?" {:response_type "ephemeral"
         :text (get-languages-page-body)}
    (let [slack-params (str/split slack-text #" ")
          language (keyword (first slack-params))]
      (let [the-thanks (ty/get-thanks language thankses)]
        (if (= :not-found the-thanks)
          {:response_type "ephemeral"
           :text (handle-language-not-found language)}
          {:response_type "in_channel"
           :text (str the-thanks
                      (if (> (count slack-params) 1)
                        (str " " (str/join " " (rest slack-params)))))})))))

(defn say-thanks-page [slack-text thankses]
  (ok json-header (get-thanks-page-body slack-text thankses)))

(defn get-unknown-languages-page-body []
  (html
    [:div
     [:h1 "Unknown Languages"]
     [:ul (for [unknown-language (keys (deref ty/unknown-languages))]
            [:li (str (name unknown-language) " = " (@ty/unknown-languages unknown-language))])]]))

(defn show-unknown-languages-page []
  (ok html-header (get-unknown-languages-page-body)))

;TODO handle-request
;TODO handle-request will return EITHER the expected value of the request or a key indicating something that went wrong -- like :not-found
(defn handle-thanks-request [])

(defn construct-thanks-response
  [thanks-response]
  thanks-response)

; TODO call handle-request then call construct-response based on return value from handle-request
; TODO put side effect of updating mongo IF the language isn't found
(defroutes app
           (GET "/" [] splash)
           (GET "/say-thanks" {params :params}
                (construct-thanks-response (say-thanks-page (params slack-text-key) ty/thankses)))
           (POST "/say-thanks" {params :params}
                (construct-thanks-response (say-thanks-page (params slack-text-key) ty/thankses)))
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