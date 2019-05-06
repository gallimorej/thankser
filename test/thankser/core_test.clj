(ns thankser.core-test
  (:require [clojure.test :refer :all]
            [thankser.core :refer :all]
            [thankser.web :refer :all]))

(deftest show-unknown-languages-test
  (testing "Testing show-unknown-languages"
    (show-unknown-languages-page)))

(deftest say-thanks-page-test
  (testing "Testing say-thanks-page"
    (say-thanks-page "hawaiian")))
