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
           :default (str (fs/cwd))
           :validate                  ; tests if --dir exists,
           {:pred dir-exists?         ; with a custom error message
            :ex-msg (fn [{:keys [value]}]
                      (str "Directory does not exist: " value))}}

   :push  {:coerce :boolean           ; defines a boolean flag
           :desc "Push when finished with build operaion"}})

(defn clean-opts [opts]
  (-> opts
      (assoc :dir (str (fs/normalize (fs/expand-home (:dir opts)))))))


(defn run [dirty-opts]
  (println "Snowcone: Snowflake Build Tool 0.1.0")
  (println "Command line arguments passed:")
  (let [opts (clean-opts dirty-opts)]
    (prn opts)

  ;; Preflight Checks
    (checks/check-if-git-repository-root (:dir opts))
    (checks/check-if-config-file-is-valid (:dir opts))

    (println "Success. Please validate output.")))



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

