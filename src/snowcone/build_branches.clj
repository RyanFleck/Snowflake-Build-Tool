(ns snowcone.build-branches
  (:require
   [snowcone.shell :refer [run-command]]
   [snowcone.gitops :as gitops]
   [snowcone.dates :refer [datestamp]]
   [babashka.fs :as fs]
   [clojure.set :as set]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [clojure.string :as s]))

;; REPL:
;; C-u C-c C-z to switch to this namespace inside the repl
;; TODO: Rebind lsp-find-definition 

(defn config-branches [{:keys [environments]}]
  (->> (vals environments)
       (mapcat (juxt :source_branch :target_branch))
       (set)))

(defn clean-notebook-path [path notebook-file]
  (-> (str (fs/path path notebook-file))
      (s/replace #"^/" "")))

(defn copy-and-transform-single-notebook
  [notebook-file
   {:keys [id notebooks path] :as notebook-data}
   {:keys [source_branch target_branch] :as target-data}
   {:keys [dir verbose offline] :as config}]
  (let [file-path (clean-notebook-path path notebook-file)]
    (println (str "Handling " file-path))

    ;; Restore file to correct location
    (gitops/restore-file file-path source_branch dir offline)
    (gitops/add-file file-path dir)
    (println (str "Restored notebook to path " file-path))
    ;; If there is an exception this will need to be rolled back

    (println (str "Finished handling " file-path))))

(defn copy-and-transform-one-notebook-set
  "Given a folder of notebooks, copy and transform all notebooks to the branch."
  [{:keys [id notebooks] :as notebook-data}
   {:keys [source_branch target_branch] :as target-data}
   {:keys [dir verbose offline] :as config}]

  (let [current-branch (gitops/get-current-branch dir)]

    ;; If not already, switch to the correct target branch.
    (when-not (= current-branch target_branch)
      (gitops/switch-branch target_branch dir)
      (println (str "Switched to branch '" target_branch "'")))

    (println (str "Handling " (count notebooks) " notebooks for " (name id) " in branch '" target_branch "'"))

    (when verbose
      (println (str (name id) " notebook data:"))
      (pprint notebook-data))

    (doseq [notebook-file notebooks]
      (copy-and-transform-single-notebook
       notebook-file notebook-data
       target-data config))

    (println "Notebook set handled.")))

(defn build-one-branch
  "Given a target branch, assemble all notebooks for that branch."
  [{:keys [id source_branch target_branch] :as target-data}
   {:keys [dir verbose offline notebooks] :as config}]

  (let [env-target-slug (s/trim (s/lower-case (name id)))
        ;; tmp-dir (str (fs/path dir (str ".tmp-" env-target-slug)))
        log-prefix (s/upper-case (str env-target-slug "-BUILD: "))]

    (when verbose
      (println (str "\nDEBUG: '" (s/upper-case env-target-slug) "' Target Data:"))
      (prn target-data))

    (println (str "\n" (s/upper-case env-target-slug) ": Building target"))

    ;; Ensure the latest commits are fetched for the source and target branches
    (when-not offline
      (gitops/fetch-branch-from-origin source_branch dir)
      (gitops/fetch-branch-from-origin target_branch dir)
      (println (str "Fetched source (" source_branch ") and target (" target_branch ") branches")))

    ;; Switch to the source branch (should not change the branch)
    (gitops/switch-branch target_branch dir)
    (println (str "Switched to branch '" target_branch "'"))

    ;; Untrack all files for a clean start in the target branch
    (when-not (empty? (gitops/list-tracked-files target_branch dir))
      (gitops/untrack-all target_branch dir))

    ;; Create .tmp-build-<slug> directory to copy files to
    ;; -p -> make if already exists
    ;; (run-command (str "mkdir -p " tmp-dir) {:dir dir})
    ;; (println "Working in directory" tmp-dir)
    (doseq [[id notebook] notebooks]
      (copy-and-transform-one-notebook-set
       (assoc notebook :id id) target-data config))

    ;; Find all notebook files and copy them to the temporary directory
    ;; ^^ Nope, trying to use

    ;; Commit
    (let [commit-msg (str "BUILD[" env-target-slug "] " (datestamp))
          lastbuild-file-path (str (fs/path dir ".lastbuild"))]

      ;; Write a 'last build' file
      (spit lastbuild-file-path commit-msg)
      (gitops/add-file lastbuild-file-path dir)

      ;; Commit the changes
      (println (str "Building with commit message: " commit-msg))
      (gitops/commit commit-msg dir))

    ;; Success.
    (println log-prefix "Finished building target")))

(defn build-branches
  "Proceeds with the construction of build branches."
  [{:keys [offline dir environments build-all-branches] :as config}]
  (println "Branching build strategy selected.")

  (when-not offline
    (gitops/fetch-all dir)
    (println "Fetched latest git branches from remote"))

  ;; Pull all branches from config and verify they exist
  (let [current-branch (gitops/get-current-branch dir)
        repo-branches (gitops/get-all-branches dir)
        required-branches (config-branches config)
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
        (build-one-branch (assoc target-data :id target-id) config)))

    ;; Switch home and signal success.
    (println "\nSuccess.")
    (gitops/switch-branch current-branch dir)
    (println (str "Completed build from '" current-branch "' branch."))))

(comment
  (build-branches t))
;; https://clojuredocs.org/clojure.core/juxt
