(ns twitter-login.views
  (:require
   [hiccup.core :refer [html]]
   [hiccup.form :as form]))

(defn login []
  (html
     [:h1 "Heya, you're gonna need to log in."]
     [:form {:method "post"}
      [:input {:type "submit" :value "login"}]
      ]))

(defn event-table [things]
  [:table
   [:tr
    [:th "Start"]
    [:th "End"]
    [:th "Text"]
    [:th]
    ]
   (for [thing things]
     [:tr
      [:td (:start thing)]
      [:td (:end thing)]
      [:td (:text thing)]
      [:td [:form {:method "post" :action "/delete"}
            (form/hidden-field :thing_id (:_id thing))
            (form/submit-button :delete)
            ]]
      ])])

(defn event-form []
  [:form {:method "post"}
   [:div
    (form/label :start "Start: ")
    (form/text-field :start)
    (form/label :start-time "Time: ")
    (form/drop-down :start-time [1 2 3 4 5] 3)
    ]
   [:div
    (form/label :end "End: ")
    (form/text-field :end)]
   [:div
    (form/label :text "Text: ")
    (form/text-field :text)]
   [:div
    (form/submit-button :add)]
   ]
  )

(defn dashboard [user events]
  (html
   [:h2 (format "Hello %s (%s)" (:name user) (:handle user))]
   (event-form)
   (event-table events)
   [:form {:method "post" :action "/logout"}
    (form/submit-button :logout)
    ]))
