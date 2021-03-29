// generated on 2020-07-29 using generator-webapp 4.0.0-8
const gulp = require('gulp');
const del = require('del');
const autoprefixer = require('autoprefixer');
const plumber = require("gulp-plumber");
const sass = require("gulp-sass");
const cssnano = require('cssnano');
const postcss = require("gulp-postcss");
const sourcemaps = require('gulp-sourcemaps');
const gulpif = require('gulp-if');
const gulpsize = require('gulp-size');

const isProd = process.env.NODE_ENV === 'production';
const isDev = !isProd;

const SERVER_RESOURCE_PATH = './resources/public/css/';
const SOURCE_RESOURCE_PATH = './scss/**/*.scss';

// Clean assets
function clean() {
    return del([SERVER_RESOURCE_PATH]);
}

function styles() {
    return gulp
        .src(SOURCE_RESOURCE_PATH)
        .pipe(gulpif(isDev, sourcemaps.init()))
        .pipe(plumber())
        .pipe(sass({ outputStyle: isDev ? "expanded" : "compressed" }))
        .pipe(gulp.dest(SERVER_RESOURCE_PATH))
        .pipe(postcss([autoprefixer([
            "> 1%",
            "last 2 versions",
            "Firefox ESR"
        ]), cssnano()]))
        .pipe(gulpif(isDev, sourcemaps.write()))
        .pipe(gulp.dest(SERVER_RESOURCE_PATH))
        .pipe(gulpsize({
            title: 'build',
            showFiles: true,
            gzip: true,
            pretty: true
        }));
}

function watchFiles() {
    gulp.watch(SOURCE_RESOURCE_PATH, styles);
}

const watch = gulp.series(styles, watchFiles);
const build = gulp.series(clean, styles);

exports.w = watch;
exports.b = build;
exports.default = watch;
