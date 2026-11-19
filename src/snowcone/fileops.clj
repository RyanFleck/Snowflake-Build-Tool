(ns snowcone.fileops
  (:require [clojure.string :as str]))

(defn replace-strings
  "Replaces every instance of each original-replacement pair in the target file."
  [text replacements]
  (reduce 
   (fn [content [original replacement]] 
     (str/replace content original replacement))
   text
   replacements))

(defn replace-strings-in-file
  "Replaces every instance of each original-replacement pair in the target file."
  [file-path replacements]

  (println (str "Making replacements in " file-path))
  (spit file-path 
        (-> (slurp file-path) (replace-strings replacements))))

(comment
  (replace-strings-in-file "test.txt" '(("one" "three")))
)
