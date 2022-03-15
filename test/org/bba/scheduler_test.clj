(ns org.bba.scheduler-test
  (:require [clojure.test :refer :all]
            [org.bba.scheduler :refer :all]))


(deftest contains-block?-test-single-blocks
  (testing "Contains Block"
    (is (and
          (= true (contains-block? {"S1AB" {:class "foo"}} "S1AB"))
          (= true (contains-block? {"S2AB" {:class "foo"}} "S2AB"))
          (= false (contains-block? {"S1AB" {:class "foo"}} "S2AB"))))))

(deftest contains-block?-test-de-blocks
  (testing "Contains Block"
    (is (and
          (= true (contains-block? {"FYD" {:class "foo"}} "S1DE"))
          (= true (contains-block? {"FYE" {:class "foo"}} "S2DE"))
          (= true (contains-block? {"S1DE" {:class "foo"}} "FYD"))
          (= true (contains-block? {"S1DE" {:class "foo"}} "FYE"))
          (= false (contains-block? {"S1DE" {:class "foo"}} "S2DE"))
          (= false (contains-block? {"S1DE" {:class "foo"}} "S1AB"))
          (= false (contains-block? {"S1AB" {:class "foo"}} "S1DE"))))))

(deftest contains-class?-test
  (testing "Schedule contains class"
    (is (and
          (= true (contains-class? {"FYD" {:criteria "foo"}} {:criteria "foo"}))
          (= false (contains-class? {"FYD" {:criteria "foo"}} {:criteria "bar"}))))))

(deftest schedule-test
  (testing "Creating a schedule"
    (is (= (list {"FYD"  {:block    "D"
                          :class    "Personal Fitness:  Foundations (Co-Ed)"
                          :criteria "Personal Fitness:  Foundations (Co-Ed)"
                          :term     "FY"}
                  "S1AB" {:block    "AB"
                          :class    "Honors Chemistry"
                          :criteria "Honors Chemistry"
                          :term     "S1"}
                  "S1C"  {:block    "C"
                          :class    "CP English 2: Literature & Comp"
                          :criteria "CP English 2: Literature & Comp"
                          :term     "S1"}
                  "S2F"  {:block    "F"
                          :class    "CP World & US History 2"
                          :criteria "CP World & US History 2"
                          :term     "S2"}}
                 {"FYD"  {:block    "D"
                          :class    "Personal Fitness:  Foundations (Co-Ed)"
                          :criteria "Personal Fitness:  Foundations (Co-Ed)"
                          :term     "FY"}
                  "S1AB" {:block    "AB"
                          :class    "Honors Chemistry"
                          :criteria "Honors Chemistry"
                          :term     "S1"}
                  "S1C"  {:block    "C"
                          :class    "Woodworking Studio"
                          :criteria "Woodworking Studio"
                          :term     "S1"}
                  "S2F"  {:block    "F"
                          :class    "CP World & US History 2"
                          :criteria "CP World & US History 2"
                          :term     "S2"}})
           (schedule [{:class "Honors Chemistry", :term "S1", :block "AB" :criteria "Honors Chemistry"}
                      {:class "CP English 2: Literature & Comp", :term "S1", :block "C" :criteria "CP English 2: Literature & Comp"}
                      {:class "CP World & US History 2", :term "S2", :block "F" :criteria "CP World & US History 2"}
                      {:class "Woodworking Studio", :term "S1", :block "C" :criteria "Woodworking Studio"}
                      {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D" :criteria "Personal Fitness:  Foundations (Co-Ed)"}])))))


(deftest schedule-class-add-pruned-remaining-test
  (testing "Schedule class and add pruned remaining classes"
    (is (= {:schedule {"S1F" {:class "Honors Chemistry", :term "S1", :block "F" :criteria {:class "Honors Chemistry"}}}
            :class-views (list {:class {:class "CP World & US History 2", :term "S2", :block "DE", :criteria {:class "CP World & US History 2"}}
                                :remaining [{:class "CP German 2/3", :term "S2", :block "C", :criteria {:class "CP German 2/3", :term "S2"}}]}
                               {:class {:class "CP German 2/3", :term "S2", :block "C", :criteria {:class "CP German 2/3", :term "S2"}}
                                :remaining [{:class "CP World & US History 2", :term "S2", :block "DE", :criteria {:class "CP World & US History 2"}}]})}

           (schedule-class-add-pruned-remaining {} {:class {:class "Honors Chemistry", :term "S1", :block "F" :criteria {:class "Honors Chemistry"}}
                                                    :remaining [{:class "CP World & US History 2", :term "S2", :block "DE", :criteria {:class "CP World & US History 2"}}
                                                                {:class "CP German 2/3", :term "S2", :block "C", :criteria {:class "CP German 2/3", :term "S2"}}
                                                                {:class "Honors Chemistry", :term "S1", :block "AB" :criteria {:class "Honors Chemistry"}}]})))))
