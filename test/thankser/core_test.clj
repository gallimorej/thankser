(ns thankser.core-test
  (:require [clojure.test :refer :all]
            [thankser.core :refer :all]))

(deftest get-thanks-test
  (testing "Testing get-thanks"
    (get-thanks :hawaiian)))

(deftest get-snark-thanks-test
  (testing "Testing get-snark-thanks"
    (get-snark-thanks)))

(deftest get-languages-test
  (testing "Testing get-languages"
    (get-languages)))
