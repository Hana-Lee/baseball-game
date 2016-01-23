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
/*global $, app, webix, $$ */

app.v_shell = (function () {
	'use strict';

	var configMap = {
		width: 1024,
		height: 750
	}, initModule, showGameRoom, showMainBoard;

	showGameRoom = function(roomId) {
		var mainLayout = $$('main-layout');
		mainLayout.removeView('main-board');
		mainLayout.addView(app.v_game_room.getView());
	};

	showMainBoard = function() {
		var mainLayout = $$('main-layout');
		mainLayout.removeView('game-room');
		mainLayout.addView(app.v_main_board.getView());
	};

	initModule = function (container) {
		webix.ui({
			container: container,
			id: 'main-layout',
			type: 'space',
			css: 'main-layout',
			height: configMap.height,
			width: configMap.width,
			rows: [
				app.v_main_board.getView()
			]
		});
	};

	return {
		initModule: initModule,
		showGameRoom: showGameRoom,
		showMainBoard: showMainBoard
	};
}());