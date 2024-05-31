(ns user
  (:require
    [remote-require.core :as rr]))

(rr/from "https://github.com/weavejester/medley/blob/master/src/medley/core.cljc"
  :require [dissoc-in])

(dissoc-in {1 {2 {3 0}}} [1 2 3])

(rr/from "https://twitter.com/nikitonsky/status/1584629264909225984"
  :require [now])

(now)

(comment
  ;; raw file
  (macroexpand-1
    '(rr/from "https://raw.githubusercontent.com/weavejester/medley/d1e00337cf6c0843fb6547aadf9ad78d981bfae5/src/medley/core.cljc"
       :require [dissoc-in assoc-some regexp?]))
    
  (rr/from "https://raw.githubusercontent.com/weavejester/medley/d1e00337cf6c0843fb6547aadf9ad78d981bfae5/src/medley/core.cljc"
    :require [dissoc-in assoc-some regexp?])

  (assoc-some {}
    :x 1
    :y nil
    :z false
    :t 2)
  
  ;; github, cross-deps
  (macroexpand-1
    '(rr/from "https://github.com/weavejester/medley/blob/master/src/medley/core.cljc"
       :require [editable? reduce-map map-keys]))
  
  (rr/from "https://github.com/weavejester/medley/blob/master/src/medley/core.cljc"
    :require [editable? reduce-map map-keys])
  
  (map-keys keyword {"a" 1 "b" 2 "c" 3})

  ;; gist
  (macroexpand-1
    '(rr/from "https://gist.githubusercontent.com/calebphillips/240dd6162aefb77584a88249b009fbe6/raw/d8b5a865e7a58bab7da48493a64db848d4f8c0f5/cal.clj"
       :require [left-pad right-pad]))

  (rr/from "https://gist.githubusercontent.com/calebphillips/240dd6162aefb77584a88249b009fbe6/raw/d8b5a865e7a58bab7da48493a64db848d4f8c0f5/cal.clj"
    :require [left-pad right-pad])

  (left-pad 50 "hello")
  (right-pad 50 "hello")
  
  ;; twitter
  (macroexpand-1
    '(rr/from "https://twitter.com/nikitonsky/status/1584629264909225984"
       :require [zip now clamp between?]))
  
  (rr/from "https://twitter.com/nikitonsky/status/1584629264909225984"
    :require [zip now clamp between?])

  (zip [1 2 3] [:a :b :c :d])
  (now)
  (clamp 0 10 100)
  (clamp 50 10 100)
  (clamp 1000 10 100)
  (between? 50 10 100)
  
  (rr/from "https://twitter.com/puredanger/status/1365163276926128128"
    :require [transpose])
  
  (transpose [[1 2 3] [4 5 6] [7 8 9]])
  
  (rr/from "https://twitter.com/nikitonsky/status/1584644295117910016"
    :require [index-of index-by])
  
  (index-of 0 [1 2 3 4])
  (index-of 3 [1 2 3 4])

  (index-by odd? [0 1 2 3 4])
  (index-by even? [1 3 5 7])
  
  (rr/from "https://twitter.com/gigasquid/status/557897741511454724"
    :require [penultimate])
  
  (penultimate [1 2 3 4 5])
  
  ;; twitter reply, macros
  (macroexpand-1
    '(rr/from "https://twitter.com/nikitonsky/status/1584634959717097472"
       :require [cond+]))
  
  (rr/from "https://twitter.com/nikitonsky/status/1584634959717097472"
    :require [cond+])
  
  (let [a -1]
    (cond+
      (= a 1) true
      :let [b (- a)]
      (= b 1) false
      :do (println a b (+ a b))
      :else nil))

  ;; error reporting
  (rr/from "https://twitter.com/nikitonsky/status/1584644295117910016")
  
  (rr/from "https://twitter.com/nikitonsky/status/1584644295117910016"
    :require [something])
  
  ;; clear cache
  (rr/clear-cache!))
