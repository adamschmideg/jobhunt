(ns jobhunt.glassdoor
  (:require [cheshire.core :as json]))

(defn params->query-string [params]
  (clojure.string/join "&" (for [[k v] params] (str (name k) "=" v))))

(def default-params {:userip "0.0.0.0"
                      :useragent ""
                      :format "json"
                      :v "1"
                      :action "employers"})

(defn companies [partnerid key query-map]
  (let [params (merge default-params
                      (assoc query-map :t.p partnerid :t.k key))
        url (str "http://api.glassdoor.com/api/api.htm?" (params->query-string params))]
    (-> url slurp (json/parse-string true))))
