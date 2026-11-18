(ns snowcone.build-branches
  (:require
   [snowcone.shell :refer [run-command]]
   [babashka.fs :as fs]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.string :as s]))

(defn config-branches [{:keys [environments]}]
  (->> (vals environments)
       (mapcat (juxt :source_branch :target_branch))
       (set)))

(defn git-repo-branches [{:keys [dir]}]
  (->> (:out (run-command "git branch" {:dir dir}))
       (s/split-lines)
       (map #(s/trim (subs % 1)))
       (set)))

(defn build-branches
  "Proceeds with the construction of build branches."
  [{:keys [offline dir] :as config}]
  (println "Branching build strategy selected.")

  (when-not offline
    (run-command "git fetch --all" {:dir dir})
    (println "Fetched latest git branches from remote"))

  ;; Pull all branches from config and verify they exist
  (let [required-branches (config-branches config)
        repo-branches (git-repo-branches config)
        missing-branches (set/difference required-branches repo-branches)]

    (println (str "Branches required: " required-branches))
    (println (str "Branches in repository: " repo-branches))

    ;; Warn if key build-branches are missing and need to be created.
    (when-not (empty? missing-branches)
      (throw (ex-info (s/join "\n" ["Git branches missing, create them: "
                                    "  git switch --orphan <branch name>"
                                    "  git commit --allow-empty -m 'creating build branch'"
                                    "  git push -u origin <branch-name>"]) 
                      {:missing-branches missing-branches})))

    (println "All required branches present. Proceeding with branch-based build."))

;; (throw (ex-info "Not yet implemented." config))
  )

(comment
  (build-branches t))
;; https://clojuredocs.org/clojure.core/juxt
