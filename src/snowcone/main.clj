(ns snowcone.main
  (:require
   [snowcone.preflight-checks :as checks]
   [babashka.cli :as cli]
   [snowcone.config :refer [get-config-data]]
   [clojure.pprint :refer [pprint]]
   [babashka.fs :as fs]))

;; Resources:
;; https://github.com/babashka/cli

(def arg-spec
  {:dir   {:alias :d
           :desc "Run as if snowcone was started in <dir>"
           :default (str (fs/cwd))
           :validate                  ; tests if --dir exists,
           {:pred fs/directory?         ; with a custom error message
            :ex-msg (fn [{:keys [value]}]
                      (str "Directory does not exist: " value))}}

   :config-name {:alias :c
                 :desc "The name of the project configuration file, default 'config.yml'"
                 :default "config.yml"}

   :push  {:coerce :boolean           ; defines a boolean flag
           :desc "Push when finished with build operaion"}})

(defn clean-opts
  "Post-processing for incoming command line arguments"
  [opts]
  (-> opts
      ;; Clean, expand, home-normalize the provided directory
      (assoc :dir (str (fs/normalize (fs/expand-home (:dir opts)))))))

(defn build-project 
  "With cleaned and validated config data, builds the Snowflake project."
  [config]

  (println "Building project with configuration data:")
  (pprint config)
)

(defn run
  "Runs the build process based on the provided configuration file"
  [input-opts]
  (println "Snowcone: Snowflake Build Tool 0.1.0")
  (println "Command line arguments passed:")
  (let [opts (clean-opts input-opts)]
    (pprint opts)

    ;; Preflight Checks
    (checks/check-if-git-repository-root (:dir opts))
    (checks/check-if-config-file-is-parsable (:dir opts))

    (let [config-data (get-config-data (:dir opts) (:config-name opts))]
      (build-project
       (-> config-data
           (assoc :cli-opts opts)
           (checks/validate-config))))

    (println "Success. Please validate output.")))

(defn -main
  "Dispatches the 'run' function with parsed CLI arguments"
  [& args]
  (cli/dispatch
   {:exec-fn run :spec arg-spec}
   args
   {:prog "snowcone" :help true}))

(defn add
  "Simple function to unit test"
  [a b]
  (+ a b))

;; ================================================================================
;; Testing
(comment
  ;; Test the main procedure
  (-main)
  (add 1 2)

  ;; To run main in a babashka script:
  ;;(apply -main *command-line-args*)
  )

