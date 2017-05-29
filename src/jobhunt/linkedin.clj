(ns jobhunt.linkedin
  (:require
    [jobhunt.utils :refer :all]
    [clojure.string :refer [lower-case]]
    [cheshire.core :as json]))

(def api-endpoint "https://www.linkedin.com/ta/federator?")

(def default-params {:types "company"})

(defn basic-company-info [name match-partial?]
  (let [params (merge default-params {:query name})
        companies (get-in
                    (read-json api-endpoint params)
                    [:company :resultList])
        lc-name (lower-case name)]
    (when companies
      (if (= 1 (count companies))
        (first companies)
        (if match-partial?
          (map :displayName (take 3 companies))
          (first
            (filter #(= lc-name
                        (lower-case (:displayName %)))
              companies)))))))
