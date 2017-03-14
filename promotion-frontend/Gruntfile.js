module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({

    	filerev: {
    	    options: {
    	        encoding: 'utf8',
    	        algorithm: 'md5',
    	        length: 8
    	    },
    	    source: {
    	        files: [{
    	            src: [
    	                'src/main/webapp/resources/build/js/**/*.js',
    	            ]
    	        }]
    	    }
    	},
        useminPrepare: {
            html: ['src/main/webapp/WEB-INF/templates/pages/**/*.html'],
            options: {
                // 测试发现这里指定的dest，是usemin引入资源的相对路径的开始
                // 在usemin中设置assetsDirs，不是指定的相对路径
                // List of directories where we should start to look for revved version of the assets referenced in the currently looked at file
                dest: 'src/main/webapp/WEB-INF/templates/build/'               // string type
            }
        },
        usemin: {
            html: ['src/main/webapp/WEB-INF/templates/build/**/*.html'],      // 注意此处是build/
            options: {
                assetsDirs: ['src/main/webapp/resources/build/js']
            }
        },
        copy: {
            html: {
                expand: true,                   // 需要该参数
                cwd: 'src/main/webapp/WEB-INF/templates/pages/',
                src: ['**/*.html'],         // 会把tpl文件夹+文件复制过去
                dest: 'src/main/webapp/WEB-INF/templates/build/'
            },
           // css: {
           //     expand: true,
           //     cwd: 'www/css/',
           //     src: ['**/*.css'],         // 会把tpl文件夹+文件复制过去
           //     dest: 'build/css'
           // },
           // img: {
           //      expand: true,
           //     cwd: 'www/img',
           //     src: ['**/*.png', '**/*.jpg', '**/*.jpeg'],         // 会把tpl文件夹+文件复制过去
           //     dest: 'build/img'
           // },
           // lib: {
            //    expand: true,
            //    cwd: 'www/lib',
            //    src: ['**/*.*'],         // 会把tpl文件夹+文件复制过去
            //    dest: 'build/lib'
            //}
        }
    });

  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-usemin');
  grunt.loadNpmTasks('grunt-filerev');
  
  

  // 最后就是顺序了，没错concat,uglify在这里哦！
  grunt.registerTask('default', [
      'copy',
      'useminPrepare',
      'filerev',
      'concat:generated',
      'uglify:generated',
      'usemin'
  ]);

};
