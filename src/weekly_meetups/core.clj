(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (:use [clj-time.core]
        [clj-time.coerce]
        [clj-time.format]
        [clj-time.local])
  (:gen-class))

;;configuration
(def number-of-weeks 1)

(def meetups
  [
   "clj-bne" 
   "qldjvm"
   "AWS-Brisbane"
   "Brisbane-Net-User-Group"
   "Brisbane-Functional-Programming-Group"
   "Brisbane-Hacks-for-Humanity"
])

;;should need to change this
(def meetup-url 
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s")


;;hacky code that does stuff
(defn- in-a-week? [event]
  (before? 
   (from-long (:time event)) 
   (plus (now) (weeks number-of-weeks)))
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
   :time (format-time (:time event))
   :url (:event_url event)
   })

(defn -main [api-key]
  (map
   format-event
   (filter 
    in-a-week?
    (get-all-meetups api-key))))

