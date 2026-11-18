(ns snowcone.build-branches
  (:require
   [snowcone.shell :refer [run-command]]
   [babashka.fs :as fs]
   [clj-yaml.core :as yaml]
   [clojure.string :as str]))

(defn build-branches
  "Proceeds with the construction of build branches."
  [config]
  (println "Branching build strategy selected.")

  ;; Collect all required branches
  (run-command "git branch")

  (throw (ex-info "Not yet implemented." config)))
