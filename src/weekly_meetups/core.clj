(ns weekly-meetups.core
  (require [clojure.data.json :as json])
  (:gen-class))


(def meetup-url 
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s")

(defn -main [api-key]
  (-> (format meetup-url api-key "clj-bne") 
      slurp
      json/read-str
      (get "results")
      ))
