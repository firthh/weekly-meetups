(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (:use [clj-time.core]
        [clj-time.coerce]
        [clj-time.format]
        [clj-time.local]
        [clostache.parser])
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
   "Brisbane-Hacks-for-Humanity"])

;;should need to change this
(def meetup-url 
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s&time=01012014,1w")

(def events-template
  (clojure.string/replace 
   (slurp "resources/events.mustache")
   "\n"
   ""))

;;hacky code that does stuff

(defn- get-meetup-events [api-key meetup]
  (-> (format meetup-url api-key meetup) 
      slurp
      (json/read-str :key-fn keyword)
      :results))

(defn- get-all-meetups [api-key]
  (->> (map #(get-meetup-events api-key %) meetups)
       flatten))

(defn- format-time [time]
   (unparse
    (with-zone (formatters :rfc822) (default-time-zone))
    (from-long time)))

(defn- format-event [event]
  {:name (:name event)
   :group_name (:name (:group event))
   :time (format-time (:time event))
   :url (:event_url event)})

(defn- events-to-html [events]
  (render events-template {:events events}))

(defn -main [api-key]
  (->> (get-all-meetups api-key)
       (map format-event)
       events-to-html))

