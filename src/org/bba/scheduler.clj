(ns org.bba.scheduler
  (:require [hickory.core :refer [parse as-hickory]]
            [hickory.select :as s]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:gen-class)
  (:import (java.io PushbackReader)))

;; Scrape classes from BBA website

(def find-classes
  (filter #(re-find #"- S1|- S2|- FY" %)))

(def classes-to-map
  (map (fn [class]
         (let [[_ class term block] (re-matches #"(.*) - (S1|S2|FY)(.*)" class)]
           {:class class :term term :block block}))))

(defn parse-classes [url]
  (let [site-htree (-> (client/get url) :body parse as-hickory)
        td-rows (s/select (s/descendant (s/tag :td)) site-htree)
        not-nil-content (filter #(not (nil? (:content %))))
        trimmed-content (map #(-> % :content first))]
    (transduce (comp not-nil-content trimmed-content find-classes classes-to-map) conj td-rows)))

(def url "https://docs.google.com/spreadsheets/u/4/d/e/2PACX-1vR5jen9QsaXcLbZbY81HE3LedUsR4UQjhBAnQCheFUp0DtK0b7bPurlVDJH8RjTFDNdzJ0uuIChWR28/pubhtml?gid=1396882618&single=true&urp=gmail_link")

(defn save-class-webpage-to-file [url]
  (let [classes-web-page (-> (client/get url) :body)]
    (with-open [w (clojure.java.io/writer "classes-webpage.edn")]
      (binding [*out* w]
        (pr classes-web-page)))))

(comment
  (def classes (parse-classes url)))

;; Steps to parse classes
(comment (def webpage (client/get url)))
(comment (def parsed-webpage (parse (:body webpage))))
(comment (def hickory-parsed (as-hickory parsed-webpage)))
(comment (def td-tags (s/select (s/descendant (s/tag :td)) hickory-parsed)))
(comment (def non-nil-td-tags (filter #(not (nil? (:content %))) td-tags)))
(comment (def just-the-content (map #(-> % :content first) non-nil-td-tags)))
(comment (def just-the-classes (filter #(re-find #"- S1|- S2|- FY" %) just-the-content)))
(comment (def classes-as-map (map (fn [class]
                                    (let [[_ class term block] (re-matches #"(.*) - (S1|S2|FY)(.*)" class)]
                                      {:class class :term term :block block})) just-the-classes)))


;; Schedule classes

(defn find-classes-in-class-list
  ([class-list {:keys [class term block] :as criteria}]
   (let [class-filter (filter #(re-find (re-pattern class) (:class %)))
         term-filter (filter  #(if (not (nil? term)) (re-find (re-pattern term) (:term %)) true))
         block-filter (filter #(if (not (nil? block)) (re-find (re-pattern block) (:block %)) true))
         add-criteria (map #(assoc % :criteria criteria))
         transducer-filter (comp class-filter term-filter block-filter add-criteria)]
     (transduce transducer-filter conj class-list))))

(defn find-requested-classes [all-classes desired-classes]
  (flatten (map #(find-classes-in-class-list all-classes %) desired-classes)))

(def valid-termblocks #{"S1AB" "S2AB" "S1C" "S2C" "FYD" "FYE" "S1F" "S2F" "S1DE" "S2DE"})

(defn contains-block? [schedule termblock]
  (let [schedule-contains-block? (some-fn #(contains? schedule %))]
    (case termblock
      "FYD" (schedule-contains-block? "FYD" "S1DE" "S2DE")
      "FYE" (schedule-contains-block? "FYE" "S1DE" "S2DE")
      "S1DE" (schedule-contains-block? "FYD" "FYE" "S1DE")
      "S2DE" (schedule-contains-block? "FYD" "FYE" "S2DE")
      (contains? schedule termblock))))

(defn contains-class? [schedule {:keys [criteria] :as class-map}]
  (apply (some-fn #(= criteria (:criteria %))) (vals schedule)))

(defn class-may-be-scheduled? [{:keys [term block class] :as class-map} schedule]
  (if (valid-termblocks (str term block))
    (and (not (contains-block? schedule (str term block)))
         (not (contains-class? schedule class-map)))
    false))

(defn schedule-class [{:keys [term block] :as class} schedule]
  (if (class-may-be-scheduled? class schedule)
    (assoc schedule (str term block) class)
    schedule))

(defn create-class-views [classes]
  (for [index (range (count classes))
        :let [classes-vec (into [] classes)
              class-map (nth classes index)
              remaining-classes (into [] (concat (subvec classes-vec 0 index) (subvec classes-vec (inc index))))]]
      {:class class-map :remaining remaining-classes}))

(defn schedule-class-add-pruned-remaining [schedule {:keys [class remaining]}]
  (let [new-schedule (schedule-class class schedule)
        new-class-views (create-class-views (filter #(class-may-be-scheduled? % new-schedule) remaining))]
    {:schedule new-schedule :class-views new-class-views}))

(defn schedule [classes]
  (let [class-views (create-class-views classes)]
    (loop [schedules [{:class-views class-views}]]
      (println "schedules: " (count schedules))
      (let [new-schedules (distinct (mapcat (fn [{:keys [schedule class-views]}]
                                              (if (not (empty? class-views))
                                                (map (partial schedule-class-add-pruned-remaining schedule) class-views)
                                                {:schedule schedule}))
                                         schedules))]
        (if (every? empty? (map :class-views schedules))
          (distinct (map :schedule schedules))
          (recur new-schedules))))))

(def desired-classes [
                      {:class "Honors Geometry"}
                      {:class "CP German 2/3" :term "S1"}
                      {:class "CP English 2: Literature & Comp" :term "S1|S2"}
                      {:class "Honors Chemistry"}
                      {:class "CP World & US History 2"}
                      {:class "CP German 2/3" :term "S2"}
                      {:class "Woodworking Studio"}
                      {:class "Personal Fitness:  Foundations \\(She/Her/They\\)"}])

(comment (def all-classes (parse-classes url)))
(comment (def rc (find-requested-classes all-classes desired-classes)))
(comment (def schedules (schedule rc)))
(comment (filter #(= 8 (count (keys %))) schedules))

(defn read-file [file]
  (edn/read (PushbackReader. (io/reader file))))


(defn -main
  "Run the scheduler
    Arguments
      1: url of class schedule
      2: file with desired classes (Regex allowed, see: desired-classes-sample.edn)"
  [& args]
  (let [url (first args)
        desired-classes-from-file (read-file (second args))
        num-of-desired-classes (count desired-classes-from-file)
        all-classes (parse-classes url)
        matching-classes (find-requested-classes all-classes desired-classes)
        schedules (schedule matching-classes)
        full-schedules (filter #(= num-of-desired-classes (count (keys %))) schedules)]
    (clojure.pprint/pprint full-schedules)))




