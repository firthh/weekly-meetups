(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (:use [clj-time.core]
        [clj-time.coerce])
  (:gen-class))


(def meetups
  ["clj-bne" "qldjvm"])

(def meetup-url 
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s")

(defn- in-a-week? [event]
  (before? (from-long (get event "time")) (plus (now) (weeks 3)))
  )

(defn- get-meetup-events [api-key meetup]
  (-> (format meetup-url api-key meetup) 
      slurp
      json/read-str
      (get "results")
      ))

(defn- get-all-meetups [api-key]
  (->>
   (map #(get-meetup-events api-key %) meetups)
   flatten
   ))

(defn -main [api-key]
  (filter 
   in-a-week?
   (get-all-meetups api-key))
    )
