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

(deftest compare-class-maps-test-equal-classes
  (testing "Compare class maps"
    (is (= true (compare-class-maps {:class "foo" :term "FY" :block "D"} {:class "foo" :term "FY" :block "D"})))))

(deftest compare-class-maps-test-equal-class-names-and-terms
  (testing "Compare class maps"
    (is (= true (compare-class-maps {:class "foo" :term "S1" :block "D"} {:class "foo" :term "S1" :block "E"})))))

(deftest compare-class-maps-test-equal-class-names-and-diff-terms
  (testing "Compare class maps"
    (is (= false (compare-class-maps {:class "foo" :term "S2" :block "AB"} {:class "foo" :term "S1" :block "DE"})))))

(deftest compare-class-maps-test-equal-class-names-and-full-year-term
  (testing "Compare class maps"
    (is (= true (compare-class-maps {:class "foo" :term "FY" :block "E"} {:class "foo" :term "S1" :block "DE"})))))


(deftest contains-class?-test
  (testing "Schedule contains class"
    (is (and
          (= true (contains-class? {"FYD" {:class "foo" :term "FY" :block "D"}} {:class "foo" :term "FY" :block "D"}))
          (= true (contains-class? {"FYD" {:class "foo" :term "FY" :block "D"}} {:class "foo" :term "S1DE" :block "D"}))
          (= false (contains-class? {"S2DE" {:class "foo" :term "S2DE" :block "D"}} {:class "foo" :term "S1DE" :block "D"}))
          (= true (contains-class? {"S1DE" {:class "foo" :term "S1DE" :block "F"}} {:class "foo" :term "S1DE" :block "D"}))))))

(deftest create-schedule-test
  (testing "Creating a schedule"
    (is (= (list {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
                  "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"},
             "S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"}}
            {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"},
             "S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"}}
            {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
             "S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
             "S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"}}
            {"S1C" {:class "Woodworking Studio", :term "S1", :block "C"},
             "S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"},
             "FYD" {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"}}
            {"S1C" {:class "CP English 2: Literature & Comp", :term "S1", :block "C"},
             "S1AB" {:class "Honors Chemistry", :term "S1", :block "AB"},
             "S2F" {:class "CP World & US History 2", :term "S2", :block "F"}})
           (distinct (create-schedule [] [{:class "Honors Chemistry", :term "S1", :block "AB"}
                                          {:class "CP English 2: Literature & Comp", :term "S1", :block "C"}
                                          {:class "CP World & US History 2", :term "S2", :block "F"}
                                          {:class "Woodworking Studio", :term "S1", :block "C"}
                                          {:class "Personal Fitness:  Foundations (Co-Ed)", :term "FY", :block "D"}]))))))


(deftest create-schedule-2-test
  (testing "test"
        (is (= (list {:class "CP English 2: Literature & Comp", :term "S1", :block "AB"}
                {:class "CP English 2: Literature & Comp", :term "S1", :block "C"}
                {:class "CP English 2: Literature & Comp", :term "S1", :block "DE"}
                {:class "CP English 2: Literature & Comp", :term "S1", :block "F"}
                {:class "CP English 2: Literature & Comp", :term "S2", :block "C"}
                {:class "CP English 2: Literature & Comp", :term "S2", :block "DE"}
                {:class "CP English 2: Literature & Comp", :term "S2", :block "F"}
                {:class "Honors Chemistry", :term "S1", :block "AB"}
                {:class "Honors Chemistry", :term "S1", :block "F"}
                {:class "Honors Chemistry", :term "S2", :block "DE"})
               (distinct (create-schedule [] [{:class "CP English 2: Literature & Comp", :term "S1", :block "AB"}
                                              {:class "CP English 2: Literature & Comp", :term "S1", :block "C"}
                                              {:class "CP English 2: Literature & Comp", :term "S1", :block "DE"}
                                              {:class "CP English 2: Literature & Comp", :term "S1", :block "F"}
                                              {:class "CP English 2: Literature & Comp", :term "S2", :block "C"}
                                              {:class "CP English 2: Literature & Comp", :term "S2", :block "DE"}
                                              {:class "CP English 2: Literature & Comp", :term "S2", :block "F"}
                                              {:class "Honors Chemistry", :term "S1", :block "AB"}
                                              {:class "Honors Chemistry", :term "S1", :block "F"}
                                              {:class "Honors Chemistry", :term "S2", :block "DE"}]))))))

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
