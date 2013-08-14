(ns twitter-login.dao
  (:require
   [monger.core :as core]
   [monger.collection :as coll]
   )
  )

(core/connect!)
(core/set-db! (core/get-db "twitter-login"))

(defn add-event [thing id]
  (coll/save "event" (merge thing {:user id})))

(defn delete-thing [id]
  (println "Deleting thing " id)
  (coll/remove-by-id "event" (org.bson.types.ObjectId. id)))

(defn get-things [id]
  (coll/find-maps "event" {:user id}))
