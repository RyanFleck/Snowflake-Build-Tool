(ns snowcone.dates)

(defn datestamp []
  (str (.format 
        (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm")
        (java.util.Date.))))


