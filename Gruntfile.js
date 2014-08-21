var shell = require('shelljs');

module.exports = function(grunt) {

    var TOMCAT_DIR = process.env.TOMCAT_HOME  || '/usr/local/tomcat';
    TOMCAT_DIR += '/';
    var STATIC_FOLDER = "src/main/webapp/WEB-INF/";

    grunt.initConfig({
        watch: {
            statics: {
                files: [STATIC_FOLDER + "**/**/*.*"],
            }
        }
    });

    grunt.event.on('watch', function(action, filepath, target) {
        if(target === 'statics'){
            var destFilePath = TOMCAT_DIR + 'webapps/ExampleLtiApp-1.0.0-SNAPSHOT/' + filepath.replace('src/main/webapp/', '');
            grunt.log.writeln(target + ': ' + filepath + ' has ' + action + ' ... and is being copied to: ' + destFilePath);
            shell.exec('cp ' + filepath + ' ' + destFilePath);
        }
    });

    grunt.loadNpmTasks('grunt-contrib-watch');

};
