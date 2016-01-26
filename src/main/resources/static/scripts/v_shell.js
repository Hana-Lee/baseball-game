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
		}, stateMap = {
			loggedIn: false
		}, showSignUp, showGameRoom, showMainBoard, initModule;

	showSignUp = function (container) {
		$$('login-container').destructor();
		app.v_sign_up.initModule(container);
	};

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
		if (stateMap.loggedIn) {
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
		} else {
			app.v_login.initModule(container);
		}
	};

	return {
		initModule: initModule,
		showSignUp: showSignUp,
		showGameRoom: showGameRoom,
		showMainBoard: showMainBoard
	};
}());