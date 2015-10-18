(ns red-dot.core
  (:gen-class)
  (:import (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)))

(defn clamp [x]
  (min 255 (max 0 (int (* 255 x)))))

(defn rgb->int [[r g b]]
  (+
    (bit-shift-left (clamp r) 16)
    (bit-shift-left (clamp g) 8)
    (clamp b)))

(defn trace []
  (repeatedly 3 rand))

(defn generate-buckets [width height bucket-size]
  (for [j (range (/ height bucket-size))
        i (range (/ width bucket-size))
        :let [x (* i bucket-size)
              y (* j bucket-size)]]
    [x y (min (+ x bucket-size) width) (min (+ y bucket-size) height)]))

(defn render-pixels [pixels image]
  (dorun (map #(let [[x y] %]
                (.setRGB image x y (rgb->int (trace)))) pixels)))

(defn render-bucket [[x1 y1 x2 y2] image]
  (let [pixels (for [x (range x1 x2) y (range y1 y2)] [x y])]
    (render-pixels pixels image)))

(defn render [width height]
  (let [image (BufferedImage. width height BufferedImage/TYPE_INT_RGB)
        buckets (generate-buckets width height 10)]
    (time (dorun (pmap #(render-bucket % image) buckets)))
    (ImageIO/write image "png" (File. "output.png"))))

(defn -main [& args]
  (render 500 500)
  (shutdown-agents))
