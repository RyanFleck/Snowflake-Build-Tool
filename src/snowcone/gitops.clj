(ns snowcone.gitops
  (:require
   [clojure.string :as s]
   [snowcone.shell :refer [run-command]]))

(defn get-current-branch [dir]
  (s/trim (:out (run-command "git branch --show-current" {:dir dir}))))

(defn switch-branch [branch dir]
  (run-command (str "git checkout " branch) {:dir dir}))


