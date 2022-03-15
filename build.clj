(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]))

(def lib 'org.bba/scheduler)
(def main 'org.bba.scheduler)

(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [opts]
  (-> opts
      (assoc :lib lib :main main)
      (bb/run-tests)
      (bb/clean)
      (bb/uber)))
