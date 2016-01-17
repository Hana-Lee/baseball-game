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
/*global $, app, webix */

app.v_game_room = (function () {
	'use strict';

	var configMap = {}, initModule, view, getView;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = [
			{template: '야구게임 v0.1', height: 45, type: 'header'},
			{template: '메뉴', height: 45},
			{
				cols: [
					{
						template: '게임룸 컨테이너',
						margin: 10,
						css: 'bbg-mr-10',
						rows: [
							{template: '게임룸'},
							{
								template: '유저정보', height: 200, cols: [
								{
									template: '유저캐릭터', width: 180, rows: [
									{template: '아이디', height: 30},
									{template: '캐릭터'}
								]
								},
								{template: '게임패드'}
							]
							}
						]
					},
					{
						template: '유저 컨테이너',
						width: 280,
						margin: 10,
						rows: [
							{template: '유저정보', height: 220},
							{
								template: '채팅창 컨테이너', rows: [
								{template: '채팅내용'},
								{template: '채팅입력', height: 30}
							]
							}
						]
					}
				]
			}
		];
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());