(ns jobhunt.utils
  (:require
    [cheshire.core :as json]))

(defn params->query-string [params]
      (clojure.string/join "&" (for [[k v] params]
                                    (str (name k)
                                         "="
                                         (java.net.URLEncoder/encode v)))))

(defn read-json [url query-map]
      (let [full-url (str url (params->query-string query-map))]
           (-> full-url slurp (json/parse-string true))))

