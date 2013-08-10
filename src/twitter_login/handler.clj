(ns twitter-login.handler
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [ring.middleware.session :as session]
   [ring.util.response :refer [response redirect content-type]]
   [hiccup.core :refer [html]]
   )
  (:import [twitter4j Twitter TwitterFactory]
           [twitter4j.conf PropertyConfiguration]))

(defn login-page []
  (content-type (response (html
                           [:form {:method "post"}
                            [:input {:type "submit" :value "login"}]
                            ]
                           ))  "text/html"
                ))

(def twitter-config
  (PropertyConfiguration. (clojure.java.io/input-stream "/Users/John/Dropbox/nuotltester.properties")))

(defn login [redirect-url]
  (let [twitter (. (TwitterFactory. twitter-config) (getInstance))
        callback-url "http://localhost:7777/callback"
        request-token (. twitter (getOAuthRequestToken callback-url))]
    (assoc
        (redirect (. request-token (getAuthenticationURL)))
      :session {:twitter twitter :request-token request-token}
      )))

(defn callback [session params]
  (let [twitter (:twitter session)
        request-token (:request-token session)
        verifier (:oauth_verifier params)]
    (. twitter (getOAuthAccessToken request-token verifier))
    (assoc
        (redirect "/")
      :session {:name (. twitter (getScreenName))}
      )
    ))

(defn logout [redirect-url]
  (assoc
   (redirect redirect-url)
   :cookies {"ring-session" {:value "" :max-age 0}}
   ))

(defn home [{name :name} login-url]
  (if (nil? name)
    (redirect login-url)
    (content-type (response (html
                             [:h2 (format "Hello %s" name)]
                             [:form {:method "post" :action "/logout"}
                              [:input {:type "submit" :value "logout"}]
                              ]
                             )) "text/html")))

(defroutes app-routes
  (GET "/" {session :session} (home session "/login"))
  (GET "/login" [] (login-page))
  (POST "/login" {params :params} (login "/"))
  (POST "/logout" []  (logout "/"))
  (GET "/callback" {session :session params :params} (callback session params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (session/wrap-session)
      ))
