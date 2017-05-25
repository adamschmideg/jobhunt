(ns jobhunt.linkedin
  (:require
    [jobhunt.utils :refer :all]
    [cheshire.core :as json]))

(def api-endpoint "https://www.linkedin.com/ta/federator?")

(def default-params {:types "company"})

(defn basic-company-info [name]
  (let [params (merge default-params {:query name})
        companies (get-in
                    (read-json api-endpoint params)
                    [:company :resultList])]
    (when companies
      (if (= 1 (count companies))
        (first companies)
        (map :displayName (take 3 companies))))))
