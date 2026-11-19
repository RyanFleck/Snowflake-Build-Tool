(ns snowcone.gitops
  (:require
   [clojure.string :as s]
   [snowcone.shell :refer [run-command]]))

(defn get-current-branch [dir]
  (s/trim (:out (run-command "git branch --show-current" {:dir dir}))))

(defn switch-branch [branch dir]
  (run-command (str "git checkout " branch) {:dir dir}))


(defn fetch-all [dir]
  (run-command "git fetch --all" {:dir dir}))

(defn fetch-branch-from-origin 
  "Fetches the latest commit for the target branch from origin."
  [branch dir]
  (run-command (str "git fetch origin " branch " --depth=1") {:dir dir}))

(defn get-all-branches [dir]
  (->> (:out (run-command "git branch" {:dir dir}))
       (s/split-lines)
       (map #(s/trim (subs % 1)))
       (set)))

(defn restore-file 
  "A method of copying a file between branches."
  [file-path source-branch dir]
  (run-command 
   (str "git restore --source=origin/" source-branch " -- " file-path) {:dir dir}))
