var gulp = require("gulp"),
    sass = require("gulp-sass"),
    concat = require("gulp-concat");

var paths = {
  scss: ["resources/public/scss/**/*.scss"],
  scssManifesto: "resources/public/scss/app.scss",
  scssOut: "resources/public/css"
};

gulp.task("scss", function () {
  gulp.src(paths.scssManifesto)
    .pipe(sass({
      includePaths: ["bower_components/foundation/scss"]
    }))
    .pipe(gulp.dest(paths.scssOut));
});

gulp.task("watch", function () {
  gulp.watch(paths.scss, ["scss"]);
});

gulp.task("default", ["scss", "watch"]);
