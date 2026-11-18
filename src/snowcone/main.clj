(ns snowcone.main
  (:require [snowcone.preflight-checks :as checks]))

(defn -main [& _args]
  (println "Snowcone: Snowflake Build Tool 0.1.0")
  (checks/check-if-git-repository-root)
  
  (println "Success."))


(defn add [a b]
  (+ a b))

(comment

  ;; Test the main procedure
  (-main))

