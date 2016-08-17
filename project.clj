(defproject cv-api "0.1.0"
  :description "API for my CV data"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [ring "1.5.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [clj-time "0.12.0"]
                 [cheshire "5.6.3"]
                 [hiccup "1.0.5"]
                 [net.mikera/imagez "0.10.0"]
                 [clj-aws-s3 "0.3.10"]
                 [environ "1.1.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler cv-api.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
