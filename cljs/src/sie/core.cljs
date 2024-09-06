(ns sie.core
  (:require [clojure.browser.dom :as dom]
            [clojure.browser.event :as event]
            [clojure.string :as s]))

(enable-console-print!)

(defn draw-image! [canvas-id img-id & {:keys [x y sx sy
                                              swidth sheight
                                              width height
                                              filters]
                                       :or {x 0 y 0}}]
  (let [canvas (dom/get-element canvas-id)
        image (dom/get-element img-id)
        ctx (.getContext canvas "2d")]
    (when filters (set! (.-filter ctx) filters))
    (.drawImage ctx image x y)))

(defn slider->filter [id-val-pairs]
  (->> (reduce (fn [result [id v]]
                 (let [filter (get {"sepiaSlider" "sepia"
                                    "hueRotateSlider" "hue-rotate"
                                    "brightnessSlider" "brightness"
                                    "contrastSlider" "contrast"
                                    "grayscaleSlider" "grayscale"
                                    "saturationSlider" "saturate"} id)
                       unit (if (= filter "hue-rotate") "deg" "%")]
                   (conj result (str filter "(" v unit ")"))))
               []
               id-val-pairs)
       (s/join " ")))

(defn handle-slider [event]
  (let [input (.-target event)
        id (.-id input)
        value (.-value input)
        filter-string (slider->filter {id value})]
    (draw-image! "canvas" "sourceImage" {:filters filter-string})))

(defn set-display! [tag v]
  (set! (.-display (.-style (js/document.querySelector tag))) v))

(defn set-image-defaults [opt]
  (let [canvas (dom/get-element "canvas")
        image (dom/get-element "sourceImage")]
    (set! (.-width canvas) (.-width image))
    (set! (.-height canvas) (.-height image))
    (set! (.-crossOrigin canvas) "anonymous")
    (draw-image! "canvas" "sourceImage" opt)))

(defn upload-image [event]
  (let [canvas (dom/get-element "canvas")
        image (dom/get-element "sourceImage")]
    (set! (.-src image) (.createObjectURL js/URL (aget (.-files event.target) 0)))
    (event/listen image "load" (fn [] (set-image-defaults {})))
    (set-display! ".help-text" "none")
    (set-display! ".image-save" "block")
    (set-display! ".image-controls" "block")
    (set-display! ".preset-filters" "block")))

(defn set-values [id-val-map]
  (doseq [[id v] id-val-map]
    (dom/set-value (keyword id) v)))

(defn reset-image []
  (let [reset-vals {"brightnessSlider" 100
                    "contrastSlider" 100
                    "grayscaleSlider" 0
                    "hueRotateSlider" 0
                    "saturationSlider" 100
                    "sepiaSlider" 0}
        filters (slider->filter reset-vals)]
    (set-values reset-vals)
    (set-image-defaults {:filters filters})))

(defn brighten []
  (let [brighten-vals {"brightnessSlider" 130
                       "contrastSlider" 120
                       "grayscaleSlider" 0
                       "hueRotateSlider" 0
                       "saturationSlider" 120
                       "sepiaSlider" 0}
        filters (slider->filter brighten-vals)]
    (set-values brighten-vals)
    (draw-image! "canvas" "sourceImage" {:filters filters})))

(defn black-white []
  (let [bw-vals {"brightnessSlider" 120
                 "contrastSlider" 120
                 "grayscaleSlider" 100
                 "hueRotateSlider" 0
                 "saturationSlider" 100
                 "sepiaSlider" 0}
        filters (slider->filter bw-vals)]
    (set-values bw-vals)
    (draw-image! "canvas" "sourceImage" {:filters filters})))

(defn funky []
  (let [funky-vals {"brightnessSlider" 100
                    "contrastSlider" 120
                    "grayscaleSlider" 0
                    "hueRotateSlider" (+ 1 (rand-int 360))
                    "saturationSlider" 100
                    "sepiaSlider" 0}
        filters (slider->filter funky-vals)]
    (set-values funky-vals)
    (draw-image! "canvas" "sourceImage" {:filters filters})))

(defn vintage []
  (let [vintage-vals {"brightnessSlider" 120
                      "contrastSlider" 100
                      "grayscaleSlider" 0
                      "hueRotateSlider" 0
                      "saturationSlider" 120
                      "sepiaSlider" 150}
        filters (slider->filter vintage-vals)]
    (set-values vintage-vals)
    (draw-image! "canvas" "sourceImage" {:filters filters})))

(defn save-image []
  (let [canvas (dom/get-element "canvas")
        link-element (dom/get-element "link")
        canvas-data (.toDataURL canvas "image/png")
        canvas-data-stream (s/replace canvas-data "image/png" "image/octet-stream")]
    (dom/set-properties :link {"download" "edited_image.png"
                               "href" canvas-data-stream})
    (.click link-element)))

(defn rotate-image [degree]
  (let [swap-hw? (= 90 degree)
        canvas (dom/get-element "canvas")
        ctx (.getContext canvas "2d")
        img (dom/get-element "sourceImage")
        width (.-width img)
        height (.-height img)
        canvas-width (.-width canvas)
        canvas-height (.-height canvas)]
    (.clearRect ctx 0 0 canvas-width canvas-height)
    (set! (.-width canvas) (if swap-hw? height width))
    (set! (.-height canvas) (if swap-hw? width height))
    (.translate ctx (/ height 2) (/ width 2))
    (.rotate ctx (* (/ js/Math.PI 180) degree))
    (.drawImage ctx img (- (/ width 2)) (- (/ height 2)))
    (.save ctx)))
