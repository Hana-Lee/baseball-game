/**
 * @author Hana Lee
 * @since 2016-01-19 19:56
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_game_room_menu = (function () {
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
				{
					id: 'exit-room', view: 'button', label: '방나가기', type: 'danger', width: configMap.button_width,
					on: {
						onItemClick: function() {
							app.v_shell.showMainBoard('game-room');
						}
					}
				},
				{id: 'room-setting', view: 'button', label: '설정', width: configMap.button_width},
				{
					width: 409
				},
				app.v_theme_selector.getView()
			]
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());