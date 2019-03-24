(defproject thankser "0.1.0-SNAPSHOT"
  :description "Thankser is a slackbot that says \"thank you\" in various languages. When just saying a simple \"thanks\" isn't enough, add some novelty by using a different tongue."
  :url "http://thankser.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.9.1"]
                 [org.clojure/data.json "0.2.6"]
                 [com.novemberain/monger "3.5.0"]
                 [compojure "1.6.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]]

  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  ;:hooks [environ.leiningen.hooks]
  :uberjar-name "thankser.jar"
  :profiles {:production {:env {:production true}}
             :dev        {:plugins [[com.jakemccrary/lein-test-refresh "LATEST"]
                                    [lein-ring "0.12.4"]
                                    [lein-ancient "0.6.15"]]}
             :uberjar    {:aot :all}}
  :ring {:handler       thankser.web/handler
         :auto-reload?  true
         :auto-refresh? true}

  :main thankser.core)