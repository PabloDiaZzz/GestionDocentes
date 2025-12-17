module.exports = {
    proxy: "localhost:8080",
    files: ["src/main/resources/**/*"],
    cors: true,
    notify: false,
    reloadDelay: 250,
    middleware: [
        function (req, res, next) {
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            res.setHeader("Pragma", "no-cache");
            res.setHeader("Expires", "0");
            next();
        }
    ]
};