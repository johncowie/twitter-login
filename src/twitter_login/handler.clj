(ns twitter-login.handler
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [ring.middleware.session :as session]
   [ring.util.response :refer [response redirect content-type]]
   [hiccup.core :refer [html]]
   ))

(defn login-page []
  (content-type (response (html
                           [:form {:method "post"}
                            [:input {:name "name" :size 30}]
                            [:input {:type "submit" :name "submit" :value "submit"}]
                            ]
                           ))  "text/html"
                ))

(defn login [{name :name} redirect-url]
  (assoc
      (redirect redirect-url)
    :session {:name name}
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
  (POST "/login" {params :params} (login params "/"))
  (POST "/logout" []  (logout "/"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (session/wrap-session)
      ))
