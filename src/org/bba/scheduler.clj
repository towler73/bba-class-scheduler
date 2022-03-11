(ns org.bba.scheduler
  (:require [hickory.core :refer [parse as-hickory]]
            [hickory.select :as s]
            [clj-http.client :as client])
  (:gen-class))

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

(comment (parse-classes url))

(comment
  (def classes (parse-classes url)))

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

(defrecord Schedule [AB C D E F DE])

(def valid-termblocks #{"S1AB" "S2AB" "S1C" "S2C" "FYD" "FYE" "S1F" "S2F" "S1DE" "S2DE"})

(defn contains-block? [schedule termblock]
  (let [schedule-contains-block? (some-fn #(contains? schedule %))]
    (cond ;; TODO switch to case
      (= "FYD" termblock) (schedule-contains-block? "FYD" "S1DE" "S2DE")
      (= "FYE" termblock) (schedule-contains-block? "FYE" "S1DE" "S2DE")
      (= "S1DE" termblock) (schedule-contains-block? "FYD" "FYE" "S1DE")
      (= "S2DE" termblock) (schedule-contains-block? "FYD" "FYE" "S2DE")
      :else (contains? schedule termblock))))

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
          ;schedules
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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println {:name (first args)}))




