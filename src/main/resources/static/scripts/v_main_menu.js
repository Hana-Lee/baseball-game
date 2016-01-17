/**
 * @author Hana Lee
 * @since 2016-01-17 21:37
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_main_menu = (function () {
	'use strict';

	var configMap = {
			height: 45,
			button_width: 200
		}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = {
			id: 'main-menu', height: configMap.height, cols: [
				{id: 'make-room', view: 'button', label: '방만들기', type: 'danger', width: configMap.button_width},
				{id: 'quick-join', view: 'button', label: '빠른입장', width: configMap.button_width}
			]
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());