(ns twitter-login.handler
  (:require
   [compojure.core :refer [defroutes GET POST DELETE]]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [ring.middleware.session :as session]
   [ring.util.response :refer [response redirect content-type]]
   [twitter-login.views :as views]
   [twitter-login.dao :as dao]
   )
  (:import [twitter4j Twitter TwitterFactory]
           [twitter4j.conf PropertyConfiguration]))

(def auth-on false)

(defn html-response [html]
  (content-type (response html) "text/html"))

(def twitter-config
  (PropertyConfiguration. (clojure.java.io/input-stream "/Users/John/Dropbox/nuotltester.properties")))

(defn login [redirect-url]
  (let [twitter (. (TwitterFactory. twitter-config) (getInstance))
        callback-url "http://localhost:7777/callback"
        request-token (. twitter (getOAuthRequestToken callback-url))]
    (assoc
        (redirect (. request-token (getAuthenticationURL)))
      :session {:twitter twitter :request-token request-token}
      ))
    )

(defn callback [session params]
  (let [twitter (:twitter session)
        request-token (:request-token session)
        verifier (:oauth_verifier params)]
    (. twitter (getOAuthAccessToken request-token verifier))
    (let [user (. twitter (showUser (. twitter (getId))))]
      (assoc
          (redirect "/")
        :session {:user {:handle (. user (getScreenName)) :name (. user (getName)) :id (. user (getId))}}))))

(defn logout [redirect-url]
  (assoc
   (redirect redirect-url)
   :cookies {"ring-session" {:value "" :max-age 0}}
   ))

(defn auth [session response]
  (if auth-on
    (if (nil? (:user session))
      (redirect "/login")
      response)
    (assoc response :session {:user {:handle "testhandle" :name "Test Name" :id 1}})
    ))

(defn post-event [{user :user} params]
  (dao/add-event params (user :id))
  (redirect "/"))

(defn delete-event [{id :thing_id}]
  (dao/delete-event id)
  (redirect "/"))



(defn get-dashboard [{user :user}]
  (html-response (views/dashboard user (dao/get-events-for-user (:id user)))))

(defroutes app-routes
  (GET "/" {session :session} (auth session (get-dashboard session) ))
  (POST "/" {session :session params :params}
        (auth session (post-event session params)))
  (POST "/delete" {session :session params :params} (delete-event params))
  (GET "/login" [] (html-response (views/login)))
  (POST "/login" {params :params} (login "/"))
  (POST "/logout" []  (logout "/"))
  (GET "/callback" {session :session params :params} (callback session params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (session/wrap-session)
      ))
