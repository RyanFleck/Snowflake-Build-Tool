(ns snowcone.preflight-checks
  (:require [babashka.process :as p]
            [babashka.fs :as fs]
            [clojure.string :as str]))

;; C-c C-k -> cider-load-buffer

(defn check-if-git-repository-root []
  (println (str "Operating at path " (fs/cwd)))
  (when-not (fs/exists? ".git")
    (throw (ex-info "Not in a git repository" {}))))

;; Testing
(comment
  (check-if-git-repository-root)

  (p/shell "ls" "-la")

  (->
   (p/shell {:out :string} "ls" "-la")
   :out
   str/split-lines)

  (fs/find-up "." ".git")
  (fs/directory? "."))
