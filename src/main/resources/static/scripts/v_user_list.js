/**
 * @author Hana Lee
 * @since 2016-01-17 22:07
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_user_list = (function () {
	'use strict';

	var configMap = {
			height: 62
		}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function (playerList) {
		//var playerList = app.v_shell.playerList;
		console.log(playerList);
		view = {
			id: 'user-list',
			view: 'list',
			select: true,
			type: {
				height: configMap.height,
				template: '<img src="images/blank_character_2.gif" width="50" height="55" style="float:left;padding-right:10px;">' +
					'<div>#nickname#(#email#)</div><div style="padding-left:18px;">점수:#totalScore.value#, 등수:#totalRank.value#</div>'
			},
			data: playerList
			//data: [
			//	{id: 1, img:'images/blank_character_2.gif', username: 'voyaging1', nickname: '이두나1', score: 1040, rank: 2},
			//	{id: 2, img:'images/blank_character_2.gif', username: 'voyaging2', nickname: '이두나2', score: 1030, rank: 3},
			//	{id: 3, img:'images/blank_character_2.gif', username: 'voyaging3', nickname: '이두나3', score: 1020, rank: 4},
			//	{id: 4, img:'images/blank_character_2.gif', username: 'voyaging4', nickname: '이두나4', score: 1010, rank: 5},
			//	{id: 5, img:'images/blank_character_2.gif', username: 'voyaging5', nickname: '이두나5', score: 1009, rank: 6},
			//	{id: 6, img:'images/blank_character_2.gif', username: 'voyaging6', nickname: '이두나6', score: 1008, rank: 7},
			//	{id: 7, img:'images/blank_character_2.gif', username: 'voyaging7', nickname: '이두나7', score: 1007, rank: 8},
			//	{id: 8, img:'images/blank_character_2.gif', username: 'voyaging8', nickname: '이두나8', score: 1006, rank: 9},
			//	{id: 9, img:'images/blank_character_2.gif', username: 'voyaging9', nickname: '이두나9', score: 1005, rank: 10},
			//	{id: 10, img:'images/blank_character_2.gif', username: 'voyaging10', nickname: '이두나10', score: 1004, rank: 11},
			//	{id: 11, img:'images/blank_character_2.gif', username: 'voyaging11', nickname: '이두나11', score: 1003, rank: 12},
			//	{id: 12, img:'images/blank_character_2.gif', username: 'voyaging12', nickname: '이두나12', score: 1002, rank: 13},
			//	{id: 13, img:'images/blank_character_2.gif', username: 'voyaging13', nickname: '이두나13', score: 1001, rank: 14}
			//]
		};

		return view;
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());