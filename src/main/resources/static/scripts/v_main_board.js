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

app.v_main_board = (function () {
	'use strict';

	var configMap = {}, initModule, view, getView;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = [
			{template: '야구게임 v0.1', height: 45, type: 'header'},
			app.v_main_menu.getView(),
			{
				cols: [
					{
						template: '게임룸 컨테이너',
						margin: 10,
						css: 'bbg-mr-10',
						rows: [
							{template: '게임룸 리스트'},
							{
								template: '채팅', height: 200, rows: [
								{template: '채팅내용'},
								{template: '채팅입력', height: 30}
							]
							}
						]
					},
					{
						template: '유저 컨테이너',
						width: 300,
						margin: 10,
						rows: [
							app.v_user_profile.getView(),
							app.v_user_list.getView()
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