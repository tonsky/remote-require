(ns remote-require.core-test
  (:require
    [remote-require.core :as rr]
    [clojure.test :refer [is are deftest testing]]))

(rr/from "https://raw.githubusercontent.com/weavejester/medley/d1e00337cf6c0843fb6547aadf9ad78d981bfae5/src/medley/core.cljc"
  :require [dissoc-in assoc-some regexp?])

(deftest test-plaintext
  (is (= {:t 2, :x 1, :z false}
        (assoc-some {}
          :x 1
          :y nil
          :z false
          :t 2))))

(rr/from "https://github.com/weavejester/medley/blob/master/src/medley/core.cljc"
  :require [editable? reduce-map map-keys dissoc-in])

(deftest test-github
  (is (= {:a 1 :b 2 :c 3} (map-keys keyword {"a" 1 "b" 2 "c" 3})))
  (is (= {} (dissoc-in {1 {2 {3 0}}} [1 2 3])))
  (is (= {1 {2 {4 1}}} (dissoc-in {1 {2 {3 0 4 1}}} [1 2 3]))))

(rr/from "https://gist.githubusercontent.com/calebphillips/240dd6162aefb77584a88249b009fbe6/raw/d8b5a865e7a58bab7da48493a64db848d4f8c0f5/cal.clj"
  :require [left-pad right-pad])

(deftest test-gist
  (is (= "                                             hello" (left-pad 50 "hello")))
  (is (= "hello                                             " (right-pad 50 "hello"))))

(rr/from "https://twitter.com/nikitonsky/status/1584629264909225984"
  :require [zip now clamp between?])

; (rr/from "https://twitter.com/puredanger/status/1365163276926128128"
;   :require [transpose])
  
; (rr/from "https://twitter.com/gigasquid/status/557897741511454724"
;   :require [penultimate])
  
(deftest twitter
  (is (= [[:a 0] [:b 1] [:c 2]] (zip [:a :b :c] (range))))
  (is (not= (now) (do (Thread/sleep 2) (now))))
  (is (= 10 (clamp 0 10 100)))
  (is (= 50 (clamp 50 10 100)))
  (is (= 100 (clamp 1000 10 100)))
  (is (= true (between? 50 10 100)))
  
  ; (is (= nil (transpose [[1 2 3] [4 5 6] [7 8 9]])))  
  ; (is (= nil (penultimate [1 2 3 4 5])))
  )

(rr/from "https://mastodon.online/@nikitonsky/112536940796403157"
  :require [index-of index-by])

(deftest mastodon
  (is (= nil (index-of 0 [1 2 3 4])))
  (is (= 2 (index-of 3 [1 2 3 4])))

  (is (= 1 (index-by odd? [0 1 2 3 4])))
  (is (= nil (index-by even? [1 3 5 7]))))

(rr/from "https://mastodon.online/@nikitonsky/112536958600169742"
  :require [cond+])

(deftest mastodon-macros
  (let [a 1]
    (is (= true (cond+
                  (= a 1) true
                  :let    [b (- a)]
                  (= b 1) false
                  :do     (+ a b)
                  :else   a))))
  
  (let [a -1]
    (is (= false (cond+
                   (= a 1) true
                   :let    [b (- a)]
                   (= b 1) false
                   :do     (+ a b)
                   :else   a))))
  
  (let [a 2]
    (is (= 2 (cond+
               (= a 1) true
               :let    [b (- a)]
               (= b 1) false
               :do     (+ a b)
               :else   a)))))

(comment
  ;; error reporting
  (rr/from "https://twitter.com/nikitonsky/status/1584644295117910016")
  
  (rr/from "https://twitter.com/nikitonsky/status/1584644295117910016"
    :require [something])
  
  ;; clear cache
  (rr/clear-cache!))
