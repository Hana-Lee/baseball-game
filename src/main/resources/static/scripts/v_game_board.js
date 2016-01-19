/**
 * @author Hana Lee
 * @since 2016-01-19 20:16
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_game_board = (function () {
	'use strict';

	var configMap = {}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = {
			id: 'board-container',
			cols: [
				{
					id: 'left-user-container', width: 220, rows: [{
					rows: [{template: '유저1', type: 'header'}, {template: '유저1 정보'}]
				}, {
					rows: [{template: '유저2', type: 'header'}, {template: '유저2 정보'}]
				}]
				},
				{
					id: 'game-progress-container',
					rows: [{template: '진행상황', type: 'header'}, {
						view: 'list',
						template: '#message#',
						data: [
							{message: '환영합니다.'},
							{message: '2/5 대기중입니다'},
							{message: '준비를 눌러주세요'},
							{message: '1번째 턴!!!'},
							{message: '숫자를 입력해주세요...'},
							{message: '1S 0B'},
							{message: '다른유저의 입력을 기다립니다'}
						]
					}]
				},
				{
					id: 'right-user-container', width: 220, rows: [{
					rows: [{template: '유저2', type: 'header'}, {template: '유저2 정보'}]
				}, {
					rows: [{template: '유저2', type: 'header'}, {template: '유저2 정보'}]
				}]
				}
			]
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());