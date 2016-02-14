/**
 * @author Hana Lee
 * @since 2016-01-17 19:35
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix, $$ */

app.v_game_room = (function () {
	'use strict';

	var configMap = {}, stateMap = {}, initModule, view, getView;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = {
			id: 'game-room',
			rows: [
				{template: '[1번방] 왕초보들 오세요', type: 'header'},
				app.v_game_room_menu.getView(),
				{
					cols: [
						{
							template: '게임룸 컨테이너',
							margin: 10,
							rows: [
								app.v_game_board.getView(),
								app.v_game_pad.getView()
							]
						},
						{
							width: 10
						},
						{
							template: '유저 컨테이너',
							width: 300,
							margin: 10,
							rows: [
								app.v_user_profile.getView(),
								app.v_game_room_chat.getView()
							]
						}
					]
				}
			]
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());