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
			height: 45
		}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = {
			id: 'main-menu', view: 'menu', height: configMap.height, data: [
				{id: 1, value: '방만들기'},
				{id: 2, value: '빠른입장'}
			], on: {
				onMenuItemClick: function (id) {
					webix.message('Click: ' + this.getMenuItem(id).value);
					app.v_login.show();
				}
			}
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());