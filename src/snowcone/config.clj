(ns snowcone.config
  (:require [babashka.process :as p]
            [babashka.fs :as fs]
            [clj-yaml.core :as yaml]
            [snowcone.gitops :as gitops]
            [clojure.string :as str]))

(defn get-config-data
  "Returns the data structure from the YAML present in config.yml"
  [directory-root config-name]
  (let [config-path (str (fs/path (fs/expand-home directory-root) config-name))]

    (when-not (fs/exists? config-path)
      (throw (ex-info "Config file 'config.yml' does not exist, add one"
                      {:root directory-root :path config-path})))

    (-> config-path
        slurp
        yaml/parse-string)))

;; ================================================================================
;; Testing
(comment
  ())
