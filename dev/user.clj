(ns user
  (:require
    [duti.core :as duti]))

(duti/set-dirs "src" "dev" "test")

(def reload
  duti/reload)

(def -main
  duti/-main)

(defn test-all []
  (duti/test #"remote-require\..*-test"))

(defn -test-main [_]
  (duti/test-exit #"remote-require\..*-test"))
