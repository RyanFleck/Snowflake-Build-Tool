(ns snowcone.build-branches
  (:require
   [snowcone.shell :refer [run-command]]
   [snowcone.gitops :as gitops]
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

(defn build-one-branch
  "Given a target branch, assemble the files."
  [target-data target-id {:keys [dir verbose] :as config}]

  (when verbose
    (println "\nUsing data to build target:")
    (prn target-data))

  (println (str "Building target '" target-id "'"))
  
  ;; Switch to the source branch
  (gitops/switch-branch (:source_branch target-data) dir)

  ;; Create .tmp-build-<slug> directory to copy files to
)

(defn build-branches
  "Proceeds with the construction of build branches."
  [{:keys [offline dir environments build-all-branches] :as config}]
  (println "Branching build strategy selected.")

  (when-not offline
    (run-command "git fetch --all" {:dir dir})
    (println "Fetched latest git branches from remote"))

  ;; Pull all branches from config and verify they exist
  (let [current-branch (gitops/get-current-branch dir)
        required-branches (config-branches config)
        repo-branches (git-repo-branches config)
        missing-branches (set/difference required-branches repo-branches)]

    ;; Not building from the config in 'development' or 'master' could cause problems
    (println (str "Configuration file is from branch: " current-branch))
    (gitops/switch-branch current-branch dir)
    (println (str "Branches required: " required-branches))
    (println (str "Branches in repository: " repo-branches))

    ;; Fail if branch name seems wrong
    ;; (when (s/starts-with? current-branch "build")
    ;;   (throw (ex-info "You appear to be inside a build branch." {:branch current-branch})))

    ;; Warn if key build-branches are missing and need to be created.
    (when-not (empty? missing-branches)
      (throw (ex-info (s/join "\n" ["Git branches missing, create them: "
                                    "  git switch --orphan <branch name>"
                                    "  git commit --allow-empty -m 'creating build branch'"
                                    "  git push -u origin <branch-name>"])
                      {:missing-branches missing-branches})))

    (println "All required branches present. Proceeding with branch-based build.")

    ;; NOTE: It would probably be smartest only to build from the
    ;; branch that the user is currently present on to ensure that the
    ;; build files listed in the config are actually present.

    ;; Filter environments by those that match the current base branch
    (let [target-envs (cond->> environments
                        (not build-all-branches)
                        (filter (fn [[_ envr]] (= current-branch (:source_branch envr)))))]

      ;; Show a message when not all branches are being assembled
      (when-not build-all-branches
        (println (str "Building only branches sourced from '" current-branch "'"
                      ", switch branches to build others or use --build-all-branches")))

    ;; We can destructure the map entry to [key value]
      (doseq [[target-id target-data] target-envs]
        (build-one-branch target-data target-id config)))

    ;; Switch home and signal success.
    (gitops/switch-branch current-branch dir)
    (println (str "Completed build from '" current-branch "' branch."))))

(comment
  (build-branches t))
;; https://clojuredocs.org/clojure.core/juxt
