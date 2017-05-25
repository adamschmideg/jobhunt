(ns jobhunt.glassdoor
  (:require
            [jobhunt.utils :refer :all]
            [clojure.data.csv :as csv]
            [com.rpl.specter :refer [select ALL multi-path]]))


(def default-params {:userip "0.0.0.0"
                      :useragent ""
                      :format "json"
                      :v "1"
                      :action "employers"})

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

(defn export-ratings [ratings csv-file]
      (let [header (map name rating-keys)]
        (with-open [out (clojure.java.io/writer csv-file)]
          (csv/write-csv out (conj ratings header)))))
