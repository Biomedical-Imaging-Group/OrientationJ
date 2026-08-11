/* Before/after image comparison: a vertical handle wipes the second image
   over the first. Markup expected (see index.md):

     <div class="oj-compare">
       <img src="..." alt="before">
       <img src="..." alt="after">
       <span class="oj-compare-line"></span>
       <input type="range" min="0" max="100" value="50" aria-label="...">
     </div>                                                              */
function ojCompare() {
  document.querySelectorAll(".oj-compare").forEach(function (box) {
    var range = box.querySelector("input[type=range]");
    if (!range || box.dataset.ojReady) return;
    var move = function () { box.style.setProperty("--pos", range.value + "%"); };
    range.addEventListener("input", move);
    box.dataset.ojReady = "1";
    move();
  });
}
document.addEventListener("DOMContentLoaded", ojCompare);
