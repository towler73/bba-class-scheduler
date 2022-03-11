(ns org.bba.core)

;; Clojure basics

;; Executing a function
(+ 2 2)

;; Defining a function

(defn add-two [value]
  (+ value 2))

(add-two 2)

;; Vectors
["A" "B" "C"]

;; define a variable
(def nums [1 2 3 4])

;; Mapping over a vector
(map (fn [n] (+ n 1)) nums)

(map #(+ % 1) nums)

(map inc nums)

;; Filtering a vectors

(filter #(> % 2) nums)

(into [] (filter #(> % 2) nums))

;; Maps

{:a 1 :b 2 :c 3 :d 4}

(def mymap {:a 1 :b 2 :c 3 :d 4})

(keys mymap)

(vals mymap)

(map inc (vals mymap))

(assoc mymap :e 5)

mymap

(dissoc mymap :a)

;; Destructuring
(defn destruct [{:keys [a b]}]
  (println "a - " a ", b - " b))

(destruct mymap)

;; Let bindings
(defn let-example [value]
  (let [a (:a value)
        b (:b value)]
    (println "a - " a ", b - " b)))

(let-example mymap)
