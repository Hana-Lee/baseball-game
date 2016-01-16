/**
 * Created by Hana on 2016-01-15.
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_shell = (function () {
	'use strict';

	var configMap = {
		width: 1024,
		height: 750
	}, initModule, makeMainLayout, makeGameRoomLayout;

	initModule = function (container) {
		//makeGameRoomLayout(container);
		makeMainLayout(container);
	};

	makeGameRoomLayout = function (container) {
		webix.ui({
			container: container,
			id: 'game-room-layout',
			type: 'space',
			css: 'main-layout',
			height: configMap.height,
			width: configMap.width,
			rows: [
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
			]
		});
	};

	makeMainLayout = function (container) {
		webix.ui({
			container: container,
			id: 'main-layout',
			type: 'space',
			css: 'main-layout',
			height: configMap.height,
			width: configMap.width,
			rows: [
				{template: '야구게임 v0.1', height: 45, type: 'header'},
				{
					id: 'main-menu', view: 'menu', height: 45, data: [
					{id: 1, value: 'Google'},
					{id: 2, value: 'Facebook'},
					{id: 3, value: 'Twitter'}
				], on: {
					onMenuItemClick: function (id) {
						webix.message('Click: ' + this.getMenuItem(id).value);
						app.v_login.show();
					}
				}
				},
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
							width: 280,
							margin: 10,
							rows: [
								{template: '유저정보', height: 220},
								{template: '유저리스트'}
							]
						}
					]
				}
			]
		});
	};

	return {
		initModule: initModule
	};
}());