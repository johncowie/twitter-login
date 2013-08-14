(ns twitter-login.views
  (:require
   [hiccup.core :refer [html]]))

(defn login []
  (html
     [:form {:method "post"}
      [:input {:type "submit" :value "login"}]
      ]
     ))

(defn things-table [things]
  [:table
   (for [thing things]
     [:tr
      [:td (:text thing)]
      [:td [:form {:method "post" :action "/delete"}
            [:input {:type :hidden :name "thing_id" :value (:_id thing)}]
            [:input {:type "submit" :value "delete"}]]]
      ]
     )]
  )

(defn dashboard [user things]
  (html
   [:h2 (format "Hello %s (%s)" (:name user) (:handle user))]
   [:form {:method "post"}
    [:input {:type :text :name "text"}]
    [:input {:type "submit" :value "add"}]
    ]
   (things-table things)
   [:form {:method "post" :action "/logout"}
    [:input {:type "submit" :value "logout"}]
    ]

   ))
