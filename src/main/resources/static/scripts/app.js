/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app */

var app = (function () {
	'use strict';
	var initModule;

	initModule = function(container) {
		app.v_shell.initModule(container);
		app.v_login.initModule();
	};

	return {
		initModule: initModule
	};
})();