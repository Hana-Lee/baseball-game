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
/*global $, app, webix */

app.v_shell = (function () {
	'use strict';

	var configMap = {
		width: 1024,
		height: 750
	}, initModule;

	initModule = function (container) {
		webix.ui({
			container: container,
			id: 'main-layout',
			type: 'space',
			css: 'main-layout',
			height: configMap.height,
			width: configMap.width,
			rows: app.v_main_board.getView()
		});
	};

	return {
		initModule: initModule
	};
}());