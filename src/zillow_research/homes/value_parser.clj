(ns zillow-research.homes.value-parser
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- splitc [line]
  (str/split line #","))

(defn- line-obj [h l]
  (into {} (map-indexed (fn [i v] [(keyword (get h i)) v]) (splitc l))))

(defn filter-dates [line] 
  (filter (fn [x y] 
            (println x)
            (re-matches #"[0-9]{4}-[0-9]{2}-[0-9]{2}" x))))

(defn lazy-csv! 
  ([file] (lazy-csv! file doall))
  ([file & transducers]
   (with-open [reader (io/reader (io/resource file))]
     (let [lseq (line-seq reader)
           header (splitc (first lseq))
           lines (map (fn [line] (line-obj header line)) (rest lseq))
           xf (apply comp transducers)]
       (transduce xf conj lines)))))

(comment 
  (def CA
    (z/lazy-csv! 
      "city-home-prices.csv" 
      (filter #(= (:StateName %) "CA")) 
      (take 5))))
