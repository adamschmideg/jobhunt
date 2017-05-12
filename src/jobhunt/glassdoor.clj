(ns jobhunt.glassdoor
  (:require [cheshire.core :as json]
            [com.rpl.specter :refer [select ALL multi-path]]))


(defn params->query-string [params]
  (clojure.string/join "&" (for [[k v] params] (str (name k) "=" v))))

(def default-params {:userip "0.0.0.0"
                      :useragent ""
                      :format "json"
                      :v "1"
                      :action "employers"})

(defn read-json [url query-map]
      (let [full-url (str url (params->query-string query-map))]
           (-> full-url slurp (json/parse-string true))))

(defn company-meta [partnerid key query-map]
      (let [params (merge default-params
                          (assoc query-map :t.p partnerid :t.k key))
            url "http://api.glassdoor.com/api/api.htm?"]
          (assoc-in (read-json url params) [:response :employers] nil)))

(defn companies [partnerid key query-map]
  (let [params (merge default-params
                      (assoc query-map :t.p partnerid :t.k key))
        url "http://api.glassdoor.com/api/api.htm?"
        page-count (get-in (company-meta partnerid key params) [:response :totalNumberOfPages])]
      (for [p (range page-count)]
         (read-json url (assoc params :pn (inc p))))))

(def rating-keys
 (list
   :name
   :sectorName
   :industry
   :numberOfRatings
   :recommendToFriendRating
   :overallRating
   :compensationAndBenefitsRating
   :seniorLeadershipRating
   :careerOpportunitiesRating
   :cultureAndValuesRating
   :workLifeBalanceRating))

(defn ratings [companies]
      (let [values (select [ALL :response :employers ALL (apply multi-path rating-keys)]
                           companies)]
        (partition (count rating-keys) values)))

