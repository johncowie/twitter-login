(defproject twitter-login "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [org.twitter4j/twitter4j-core "3.0.3"]
                 [hiccup "1.0.4"]
                 [com.novemberain/monger "1.4.0"]
                 ]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler twitter-login.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
