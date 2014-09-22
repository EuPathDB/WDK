var wdkFiles = require('../wdkFiles');
var filterByFlag = require('./helpers').filterByFlag;

var glob = require('../node_modules/grunt/node_modules/glob');
var externalRegex = /^(https?:)?\/\//;

function expandGlob(files, pattern) {
  return externalRegex.test(pattern)
    ? files.concat(pattern)
    : files.concat(glob.sync(pattern));
}

module.exports = function(grunt) {
  grunt.registerTask('debugScript', 'Generate script tags for WDK files to load individually', function(dest) {
    var scripts = [].concat(
      filterByFlag('env', 'dev', wdkFiles.libs).reduce(expandGlob, []),
      'wdk/js/wdk.templates.js',
      filterByFlag('env', 'dev', wdkFiles.src).reduce(expandGlob, [])
    );

    var scriptLoaderStr = scripts.map(function(script) {
      var line;

      // remove file base
      script = script.replace(/^webapp\//, '');

      if (externalRegex.test(script)) {
        line = 'document.writeln(\'<script src="' + script + '">\\x3c/script>\');\n';
      } else {
        line = 'document.writeln(\'<script src="\' + wdkConfig.assetsUrl + \'/' + script + '">\\x3c/script>\');\n';
      }
      return line;
    }).join('');

    grunt.file.write(dest, scriptLoaderStr);

  });
};
