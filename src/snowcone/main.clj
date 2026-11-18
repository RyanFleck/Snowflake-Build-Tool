(ns snowcone.main
  (:require 
   [snowcone.preflight-checks :as checks]
   [babashka.cli :as cli]
   [babashka.fs :as fs]))

;; Resources:
;; https://github.com/babashka/cli

;; (defn -main [& _args]
;;   (println "Snowcone: Snowflake Build Tool 0.1.0")
;;   (checks/check-if-git-repository-root)
  
;;   (println "Success."))


(defn dir-exists? [path]
  (fs/directory? path))



(def arg-spec
  {:dir   {:alias :C                  ; like git's own -C
           :desc "Run as if snowcone was started in <dir>"
           :validate                  ; tests if --dir exists,
           {:pred dir-exists?         ; with a custom error message
            :ex-msg (fn [{:keys [value]}]
                      (str "Directory does not exist: " value))}}


   :push  {:coerce :boolean           ; defines a boolean flag
           :desc "Push when finished with build operaion"}})



(defn run [opts]
  (println "Snowcone: Snowflake Build Tool 0.1.0")
  (println "Here are your cli args!:" opts)

  ;; Preflight Checks
  (checks/check-if-git-repository-root (fs/cwd))
  (checks/check-if-config-file-is-valid (fs/cwd))

  (println "Success. Please validate output."))



(defn -main [& args]
  (cli/dispatch 
   {:exec-fn run :spec arg-spec} 
   args
   {:prog "snowcone" :help true}))



;; For testing unit testing
(defn add [a b]
  (+ a b))



(comment
  ;; Test the main procedure
  (-main)

  ;; To run main in a babashka script:
  ;;(apply -main *command-line-args*)
  )

