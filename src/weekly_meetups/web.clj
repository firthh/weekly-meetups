(ns weekly-meetups.web
  (:use compojure.core
        ring.util.response)
  (:require [weekly-meetups.core :as core]
            [compojure.handler :as handler]))

(defroutes app-routes
  (GET "/:api-key" [api-key] (core/events-to-html (core/get-events api-key))))

(def app
  (handler/site app-routes))
