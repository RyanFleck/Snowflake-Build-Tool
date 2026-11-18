(ns snowcone.shell
  (:require [babashka.process :as p]
            [clojure.string :as s]))

(defn split-to-list 
  "Splits a passed command into a list of strings for input to p/shell"
  [^String cmd]
  (remove s/blank? (s/split cmd #" ")))

(defn run-command
  "Runs a command, raises an exception if an error is encountered."
  [^String cmd]

  (println (str "Running command '" cmd "'"))
  (apply p/shell  
         {:out :string :err :string}
         (split-to-list cmd)))

;; ================================================================================
;; Testing
(comment
  (s/split "sadf asfd" #" ")

  (run-command "git pull")

  (split-to-list "git   pull"))
