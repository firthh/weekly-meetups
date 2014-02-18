(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (:use [clj-time.core]
        [clj-time.coerce]
        [clj-time.format]
        [clj-time.local])
  (:gen-class))


(def meetups
  ["clj-bne" "qldjvm"])

(def meetup-url 
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s")

(defn- in-a-week? [event]
  (before? 
   (from-long (:time event)) 
   (plus (now) (weeks 5)))
  )

(defn- get-meetup-events [api-key meetup]
  (-> (format meetup-url api-key meetup) 
      slurp
      (json/read-str :key-fn keyword)
      :results))

(defn- get-all-meetups [api-key]
  (->>
   (map #(get-meetup-events api-key %) meetups)
   flatten
   ))

(defn- format-time [time]
   (unparse
          (with-zone (formatters :rfc822) (default-time-zone))
          (from-long time)
          ))

(defn- format-event [event]
  { 
   :name (:name event)
   :group_name (:name (:group event))
   :time (format-time (:time event))})

(defn -main [api-key]
  (map
   format-event
   (filter 
    in-a-week?
    (get-all-meetups api-key))))

