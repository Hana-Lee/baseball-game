/**
 * @author Hana Lee
 * @since 2016-01-17 22:33
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_game_list = (function () {
	'use strict';

	var configMap = {}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = {
			id: 'game-room-list',
			view: 'dataview',
			select: true,
			type: {
				height: 128,
				//width: 215,
				templateStart: '<div class="custom_item">',
				template: '<div class="webix_strong">#name#</div>#user_count#/5, 횟수: #guess_num#, 갯수: #gen_num#<div>방장: #owner#</div><button class="join-room" data-room-id="#id#" style="float: right;">입장</button>',
				templateEnd: '</div>'
			},
			data: [
				{id: 1, name: '게임룸1', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나1'},
				{id: 2, name: '게임룸2', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나2'},
				{id: 3, name: '게임룸3', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나3'},
				{id: 4, name: '게임룸4', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나4'},
				{id: 5, name: '게임룸5', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나5'},
				{id: 6, name: '게임룸6', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나6'},
				{id: 7, name: '게임룸7', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나7'},
				{id: 8, name: '게임룸8', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나8'},
				{id: 9, name: '게임룸9', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나9'},
				{id: 10, name: '게임룸10', user_count: 4, guess_num: 10, gen_num: 3, owner: '이하나10'}
			],
			ready: function() {
				$('.join-room').click(function (evt) {
					var target = evt.target, roomId;
					roomId = target.getAttribute('data-room-id');
					app.v_shell.showGameRoom(roomId);
				});
			}
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());