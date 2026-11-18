(ns snowcone.test-a
  (:require 
   [snowcone.main :refer [add]]
   [clojure.test :refer [deftest is testing]]))

(deftest addition-test
  (testing "add two numbers"
    (is (= 3 (add 1 2)))))

