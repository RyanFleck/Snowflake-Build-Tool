(ns snowcone.shell
  (:require [babashka.process :as p]
            [clojure.string :as s]))


(defn run-command 
  "Simplify the execution of git commands"
  [cmd]
  "Runs a command, raises an exception if an error is encountered."
  (p/shell (s/split cmd #" ")))


;; ================================================================================
;; Testing
(comment
  (s/split "sadf asfd" #" ")

  (run-command [])
)
