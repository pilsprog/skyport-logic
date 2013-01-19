(defun skyport-generate-map (n default)
  "Generate a n x n hexagonal tiling in ascii art and insert into buffer. Made for the TG2013 AI competition."
  (interactive "nLength in J dimension: \nsDefault fill: ")
  (if (y-or-n-p "Generate header? ") (skyport-generate-map:generate-header n))
  (message "Generating %d x %d grid with default letter '%s'" n n default)
  (loop for i from 1 to n do (skyport-generate-map:generate-increasing-line i n default))
  (loop for i from n downto 1 do (skyport-generate-map:generate-decreasing-line i n default)))

(defun skyport-generate-map:generate-header (size)
  (insert (format
   "skyport map format 1 # Format version
ignore /\\_           # Characters to ignore (whitespace is always ignored)
players 2            # This map is for two players
size %dx%d           # Size (from the top corner.) This has to be correct.
description \"testmap for two players\"
spawn S              # S denotes a spawnpoint
grass G              # G is grass
cogs  C              # C are cogs
rubidium R           # R is rubidium
explosium E          # E is explosium
void V               # V is void
rocks O              # O are rocks (\"obstacle\") 
" size size))
  (redisplay))

(defun skyport-generate-map:generate-increasing-line (length lines letter)
  (insert (mapconcat 'identity (make-list (+ (- lines length) 1) "   ") ""))
  (insert (mapconcat 'identity (make-list length "__") (format "/%s \\" letter)))
  (insert "\n")
  (redisplay))

(defun skyport-generate-map:generate-decreasing-line (length lines letter)
  (insert "  ")
  (if (not (eq length lines))
      (insert (mapconcat 'identity (make-list (- lines length 1) "   ") "") "\\__")
    (insert (mapconcat 'identity (make-list (- lines length) "   ") "")))
    
  (insert (mapconcat 'identity (make-list length (format "/%s \\" letter)) "__"))
  (if (not (eq length lines))
      (insert "__/"))
  (insert "\n")
  (if (eq length 1)
      (insert (mapconcat 'identity (make-list (- lines 1) "   ") "") "  \\__/"))
  (redisplay))
