/**
 * @author Hana Lee
 * @since 2016-01-15 19:35
 */
/*jslint        browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true, todo    : true
 */
/*global $, app, webix, $$ */

app.v_shell = (function () {
	'use strict';

	var configMap = {
			width: 1024,
			height: 750
		}, stateMap = {
			loggedIn: false,
			container: ''
		}, showSignUp, showLogin, showGameRoom, showMainBoard, initModule;

	showSignUp = function () {
		$$('login-container').destructor();
		app.v_sign_up.initModule(stateMap.container);
	};

	showLogin = function() {
		$$('sign-up-container').destructor();
		app.v_login.initModule(stateMap.container);
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
		stateMap.container = container;
		if (stateMap.loggedIn) {
			webix.ui({
				container: stateMap.container,
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
			app.v_sign_up.initModule(stateMap.container);
			//app.v_login.initModule(stateMap.container);
		}
	};

	return {
		initModule: initModule,
		showSignUp: showSignUp,
		showLogin: showLogin,
		showGameRoom: showGameRoom,
		showMainBoard: showMainBoard
	};
}());