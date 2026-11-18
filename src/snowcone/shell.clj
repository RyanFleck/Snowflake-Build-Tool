(ns snowcone.shell
  (:require [babashka.process :as p]
            [clojure.string :as s]))

(defn run-command [cmd]
  "Runs a command, raises an exception if an error is encountered."
  (p/shell (s/split cmd #" ")))

(comment
  (s/split "sadf asfd" #" ")
)
