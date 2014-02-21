(ns weekly-meetups.web
  (:use compojure.core
        ring.util.response)
  (:require [weekly-meetups.core :as core]
            [compojure.handler :as handler]))

(defroutes app-routes
  (GET "/api-key/:api-key" [api-key] 
       (core/events-to-html (core/get-events api-key :brisbane)))
  (GET "/api-key/:api-key/city/:city" [api-key city]
       (core/events-to-html (core/get-events api-key (keyword city))))
  (GET "/" [] "Hello"))

(def app
  (handler/site app-routes))
