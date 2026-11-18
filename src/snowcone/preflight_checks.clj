(ns snowcone.preflight-checks
  (:require [babashka.process :as p]
            [babashka.fs :as fs]
            [clojure.string :as str]))

;; C-c C-k -> cider-load-buffer

;; Resources:
;; https://github.com/babashka/fs

(defn check-if-git-repository-root [path]
  (println (str "Operating at path " path))
  (when-not (fs/exists? (fs/path path ".git"))
    (throw (ex-info "Not in a git repository" {}))))


(defn check-if-config-file-is-valid [path]
  (println (str "Checking for config file."))
  (when-not (fs/exists? (fs/path path "config.yml"))
    (throw (ex-info "Config file 'config.yml' does not exist, add one." {}))))

;; Testing
(comment
  (check-if-git-repository-root (fs/cwd))
  (check-if-config-file-is-valid (fs/cwd))

(fs/exists? (fs/path (fs/cwd) "config.yml"))

(fs/exists? (fs/path (fs/cwd) "config.yml"))

  (p/shell "ls" "-la")

  (->
   (p/shell {:out :string} "ls" "-la")
   :out
   str/split-lines)

  (fs/find-up "." ".git")
  (fs/directory? "."))
