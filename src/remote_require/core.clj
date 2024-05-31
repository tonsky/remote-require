(ns remote-require.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.tools.reader :as reader]
    [clojure.tools.reader.reader-types :as reader-types])
  (:import
    [java.io File]
    [java.net URI]
    [java.util.regex Pattern]))

(defn rewrite-url [url]
  (condp re-matches url
    #"https://github\.com/([^/]+)/([^/]+)/blob/([^/]+)/(.*)"
    :>> (fn [[_ user repo tree path]]
          (format "https://raw.githubusercontent.com/%s/%s/%s/%s" user repo tree path))

    #"https://(?:mobile\.)?twitter\.com/@?([^/]+)/status/(\d+)/?"
    :>> (fn [[_ user id]]
          (format "https://nitter.privacydev.net/%s/status/%s" user id))
    
    #"https://(.*mastodon.*)/([^/]+)/(\d+)/?"
    :>> (fn [[_ domain user id]]
          (format "https://%s/%s/%s/embed" domain user id))
    
    url))

(defn content-impl [url]
  (let [uri  (URI. url)
        file (io/file (str (System/getenv "HOME") "/.clj-remote-require/" (.getHost uri) (.getPath uri)))]
    (if (.exists file)
      (slurp file)
      (let [content (slurp url)
            content (if-some [i (str/index-of content "<body>")]
                      (subs content (+ i 6))
                      content)
            content (if-some [i (str/index-of content "</body>")]
                      (subs content 0 i)
                      content)]
        (.mkdirs (.getParentFile file))
        (spit file content)
        content))))

(defn content [url]
  (-> (rewrite-url url)
    (content-impl)
    (str/replace #"<[^>]+>" "")
    (str/replace "&lt;" "<")
    (str/replace "&gt;" ">")
    (str/replace "&quot;" "\"")
    (str/replace "&apos;" "'")
    (str/replace "&amp;" "&")))

(defn read-clojure
  ([s]
   (binding [reader/*read-eval* false
             reader/*alias-map* {}]
     (reader/read
       {:read-cond :preserve}
       (reader-types/indexing-push-back-reader s))))
  ([s feature]
   (binding [reader/*read-eval* false
             reader/*alias-map* {}]
     (reader/read
       {:read-cond :allow
        :features #{feature}}
       (reader-types/indexing-push-back-reader s)))))

(defn import-sym [content sym]
  (let [pattern (re-pattern (str "\\(def[-A-Za-z0-9!?]+\\s+\\Q" sym "\\E\\s"))
        matcher (re-matcher pattern content)]
    (if (.find matcher)
      (let [end  (subs content (.start matcher))
            form (read-clojure end :clj)]
        form)
      (throw (IllegalArgumentException. (format "Can't find '%s'" sym))))))

(defn import-syms [url syms]
  (when-not (seq syms) 
    (throw (IllegalArgumentException. ":require is required")))
  
  (let [content (content url)]
    (list* `do
      (keep #(import-sym content %) syms))))
  
(defmacro from [url & {:as args}]
  (import-syms url (:require args)))

(defn clear-cache! []
  (doseq [f (->> (io/file (str (System/getenv "HOME") "/.clj-remote-require"))
              (file-seq)
              (reverse))]
    (.delete ^File f)))
