function drawImage(canvasId, imgId, options = {}) {
  const { x = 0, y = 0, filters } = options;
  const canvas = document.getElementById(canvasId);
  const image = document.getElementById(imgId);
  const ctx = canvas.getContext("2d");

  if (filters) {
    ctx.filter = filters;
  }

  ctx.drawImage(image, x, y);
}

function sliderToFilter(idValPairs) {
  const filterMap = {
    "sepiaSlider": "sepia",
    "hueRotateSlider": "hue-rotate",
    "brightnessSlider": "brightness",
    "contrastSlider": "contrast",
    "grayscaleSlider": "grayscale",
    "saturationSlider": "saturate"
  };

  const result = idValPairs.reduce((acc, [id, v]) => {
    const filter = filterMap[id];
    const unit = filter === "hue-rotate" ? "deg" : "%";
    return [...acc, `${filter}(${v}${unit})`];
  }, []);

  return result.join(" ");
}

function handleSlider(event) {
  const input = event.target;
  const id = input.id;
  const value = input.value;
  const filterString = sliderToFilter([[id, value]]);
  drawImage("canvas", "sourceImage", { filters: filterString });
}

function setDisplay(tag, v) {
  document.querySelector(tag).style.display = v;
}

function setImageDefaults(opt) {
  const canvas = document.getElementById("canvas");
  const image = document.getElementById("sourceImage");
  canvas.width = image.width;
  canvas.height = image.height;
  canvas.crossOrigin = "anonymous";
  drawImage("canvas", "sourceImage", opt);
}

function uploadImage(event) {
  const canvas = document.getElementById("canvas");
  const image = document.getElementById("sourceImage");
  image.src = URL.createObjectURL(event.target.files[0]);
  image.addEventListener("load", () => setImageDefaults({}));
  setDisplay(".help-text", "none");
  setDisplay(".image-save", "block");
  setDisplay(".image-controls", "block");
  setDisplay(".preset-filters", "block");
}

function setValues(idValMap) {
  for (const [id, v] of Object.entries(idValMap)) {
    document.getElementById(id).value = v;
  }
}

function resetImage() {
  const resetVals = {
    "brightnessSlider": 100,
    "contrastSlider": 100,
    "grayscaleSlider": 0,
    "hueRotateSlider": 0,
    "saturationSlider": 100,
    "sepiaSlider": 0
  };
  const filters = sliderToFilter(Object.entries(resetVals));
  setValues(resetVals);
  setImageDefaults({ filters });
}

function brighten() {
  const brightenVals = {
    "brightnessSlider": 130,
    "contrastSlider": 120,
    "grayscaleSlider": 0,
    "hueRotateSlider": 0,
    "saturationSlider": 120,
    "sepiaSlider": 0
  };
  const filters = sliderToFilter(Object.entries(brightenVals));
  setValues(brightenVals);
  drawImage("canvas", "sourceImage", { filters });
}

function blackWhite() {
  const bwVals = {
    "brightnessSlider": 120,
    "contrastSlider": 120,
    "grayscaleSlider": 100,
    "hueRotateSlider": 0,
    "saturationSlider": 100,
    "sepiaSlider": 0
  };
  const filters = sliderToFilter(Object.entries(bwVals));
  setValues(bwVals);
  drawImage("canvas", "sourceImage", { filters });
}

function funky() {
  const funkyVals = {
    "brightnessSlider": 100,
    "contrastSlider": 120,
    "grayscaleSlider": 0,
    "hueRotateSlider": 1 + Math.floor(Math.random() * 360),
    "saturationSlider": 100,
    "sepiaSlider": 0
  };
  const filters = sliderToFilter(Object.entries(funkyVals));
  setValues(funkyVals);
  drawImage("canvas", "sourceImage", { filters });
}

function vintage() {
  const vintageVals = {
    "brightnessSlider": 120,
    "contrastSlider": 100,
    "grayscaleSlider": 0,
    "hueRotateSlider": 0,
    "saturationSlider": 120,
    "sepiaSlider": 150
  };
  const filters = sliderToFilter(Object.entries(vintageVals));
  setValues(vintageVals);
  drawImage("canvas", "sourceImage", { filters });
}

function saveImage() {
  const canvas = document.getElementById("canvas");
  const linkElement = document.getElementById("link");
  const canvasData = canvas.toDataURL("image/png");
  const canvasDataStream = canvasData.replace("image/png", "image/octet-stream");
  linkElement.download = "edited_image.png";
  linkElement.href = canvasDataStream;
  linkElement.click();
}

function rotateImage(degree) {
  const swapHW = degree === 90;
  const canvas = document.getElementById("canvas");
  const ctx = canvas.getContext("2d");
  const img = document.getElementById("sourceImage");
  const width = img.width;
  const height = img.height;
  const canvasWidth = canvas.width;
  const canvasHeight = canvas.height;

  ctx.clearRect(0, 0, canvasWidth, canvasHeight);
  canvas.width = swapHW ? height : width;
  canvas.height = swapHW ? width : height;
  ctx.translate(height / 2, width / 2);
  ctx.rotate(degree * Math.PI / 180);
  ctx.drawImage(img, -width / 2, -height / 2);
  ctx.save();
}
