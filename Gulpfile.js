var gulp = require("gulp"),
    sass = require("gulp-sass"),
    concat = require("gulp-concat");

var paths = {
  scss: ["resources/public/scss/**/*.scss"],
  scssOut: "resources/public/css"
};

gulp.task("scss", function () {
  gulp.src(paths.scss)
    .pipe(sass({
      includePaths: ["bower_components/foundation/scss"]
    }))
    .pipe(concat("app.css"))
    .pipe(gulp.dest(paths.scssOut));
});

gulp.task("watch", function () {
  gulp.watch(paths.scss, ["scss"]);
});

gulp.task("default", ["scss", "watch"]);
