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
		}, player = null, playerList = [],
		showSignUp, showLogin, showGameRoom, showMainBoard, logout,
		_getLoggedInPlayerInfo, _getLoggedInPlayers,
		initModule;

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

	showMainBoard = function(removeContainer) {
		var mainLayout;
		if (removeContainer === 'login-container') {
			$$('login-container').destructor();
			$('#main-container').html('');
			stateMap.loggedIn = true;
			initModule(stateMap.container);
		} else {
			mainLayout = $$('main-layout');
			mainLayout.removeView('game-room');
			mainLayout.addView(app.v_main_board.getView());
		}
	};

	logout = function() {
		webix.ajax().post('logout', {
			error: function(text) {
				var textJson = JSON.parse(text);
				webix.alert({
					title: '오류',
					ok: '확인',
					text: textJson.message
				});

			},
			success: function() {
				$$('main-board').destructor();
				stateMap.loggedIn = false;
				$('#main-container').html('');
				initModule(stateMap.container);
			}
		});
	};

	_getLoggedInPlayerInfo = function () {
		var serverResponse = '';
		webix.ajax().sync().get('player', {
			error: function(text) {
				serverResponse = JSON.parse(text);
			},
			success: function(text) {
				serverResponse = JSON.parse(text);
			}
		});

		return serverResponse;
	};

	_getLoggedInPlayers = function () {
		var serverResponse = '';
		webix.ajax().sync().get('player/login/true', {
			error: function(text) {
				serverResponse = JSON.parse(text);
			},
			success: function(text) {
				serverResponse = JSON.parse(text);
			}
		});

		return serverResponse;
	};

	initModule = function (container) {
		console.log('shell')
		var playerResult, playerListResult;
		playerResult = _getLoggedInPlayerInfo();
		playerListResult = _getLoggedInPlayers();

		if (playerResult && playerResult.status && playerResult.status === 401) {
			stateMap.loggedIn = false;
		} else if (playerResult && playerResult.email) {
			stateMap.loggedIn = true;
		}

		if (stateMap.loggedIn) {
			player = playerResult;
		}

		if (playerListResult.status && playerListResult.status === 401) {
			playerList = [];
		} else {
			playerList = playerListResult;
		}

		console.log(playerResult);
		console.log(playerListResult);

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
			//app.v_sign_up.initModule(stateMap.container);
			app.v_login.initModule(stateMap.container);
		}
	};

	return {
		initModule: initModule,
		showSignUp: showSignUp,
		showLogin: showLogin,
		showGameRoom: showGameRoom,
		showMainBoard: showMainBoard,
		logout: logout,
		player: player,
		playerList: playerList
	};
}());