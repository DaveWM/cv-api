(ns cv-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [cheshire.core :refer [generate-string]]
            [cheshire.generate :refer [add-encoder]]
            [clj-time.format :as f]
            [cv-api.data :refer [cv-data]]))

(add-encoder org.joda.time.DateTime
             (fn [date jsonGenerator]
               (.writeString jsonGenerator (f/unparse (f/formatters :basic-date-time) date))))

(defn get-cv-json [request]
  {:status 200
   :headers {"content-type" "application/json"}
   :body (generate-string cv-data)})

(defroutes app-routes
  (GET "/api/cv" [] get-cv-json)
  (route/resources "/resources/public")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-reload)
      (wrap-defaults site-defaults)))
