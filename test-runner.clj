#!/usr/bin/env bb

;; See https://book.babashka.org/#_running_tests

(require '[clojure.test :as t]
         '[babashka.classpath :as cp])

(cp/add-classpath "src:test")                        

;; (require 'snowcone.test-a 'your.test-b)                  
(require 'snowcone.test-a)                  

(def test-results
  (t/run-tests 'snowcone.test-a))           

(let [{:keys [fail error]} test-results]
  (when (pos? (+ fail error))
    (System/exit 1)))  
