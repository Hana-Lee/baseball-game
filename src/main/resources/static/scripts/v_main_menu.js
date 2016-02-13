/**
 * @author Hana Lee
 * @since 2016-01-17 21:37
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true, todo    : true
 */
/*global $, app, webix, $$ */

app.v_main_menu = (function () {
	'use strict';

	var configMap = {
			height: 45,
			button_width: 200
		}, getView, showCreateGameRoomDialog, getCreateFormView, initModule,
		view;

	getView = function () {
		return view;
	};

	showCreateGameRoomDialog = function () {
		webix.ui({
			view: 'window',
			id: 'create-game-room-window',
			head: '게임룸 생성',
			height: 400,
			width: 300,
			position: 'center',
			modal: true,
			body: getCreateFormView()
		}).show();
	};

	getCreateFormView = function () {
		return {
			id: 'create-game-room-form',
			view: 'form',
			borderless: true,
			elements: [
				{view: 'text', label: '이름', name: 'name', invalidMessage: '이름을 입력해주세요'},
				{
					view: 'fieldset', label: '게임 설정',
					body: {
						rows: [
							{
								view: 'richselect',
								label: '숫자갯수',
								name: 'generationNumberCount',
								invalidMessage: '숫자 갯수를 정해주세요',
								value: 3,
								options: [
									{id: 2, value: '2 개'},
									{id: 3, value: '3 개'},
									{id: 4, value: '4 개'},
									{id: 5, value: '5 개'}
								]
							},
							{
								view: 'richselect',
								label: '입력횟수',
								name: 'limitGuessInputCount',
								invalidMessage: '입력 횟수를 정해주세요',
								value: 10,
								options: [
									{id: 1, value: '1 회'},
									{id: 5, value: '5 회'},
									{id: 10, value: '10 회'},
									{id: 15, value: '15 회'},
									{id: 20, value: '20 회'}
								]
							},
							{
								view: 'richselect',
								label: '입력오류횟수',
								name: 'limitWrongInputCount',
								invalidMessage: '입력 오류 횟수를 정해주세요',
								value: 5,
								options: [
									{id: 5, value: '5 회'},
									{id: 10, value: '10 회'},
									{id: 15, value: '15 회'},
									{id: 20, value: '20 회'}
								]
							}
						]
					}
				},
				{
					view: 'richselect', label: '역할', name: 'gameRole', invalidMessage: '역할을 선택해주세요', value: 'ATTACKER',
					options: [
						{id: 'ATTACKER', value: '공격'},
						{id: 'DEFENDER', value: '수비'}
					]
				},
				{
					cols: [
						{
							view: 'button', value: '생성', hotkey: 'enter',
							click: function () {
								if ($$('create-game-room-form').validate()) { //validate form
									this.getTopParentView().close();
									// TODO: 생성된 게임룸으로 입장 하는 코드 작성하기
								} else {
									webix.message({type: 'error', text: 'Form data is invalid'});
								}
							}
						},
						{
							view: 'button', value: '닫기',
							click: function () {
								this.getTopParentView().close();
							}
						}
					]
				}
			],
			rules: {
				name: webix.rules.isNotEmpty
			},
			elementsConfig: {
				labelPosition: 'left'
			}
		};
	};

	initModule = function () {
		view = {
			id: 'main-menu', height: configMap.height, cols: [
				{
					id: 'make-room', view: 'button', label: '방만들기', type: 'danger', width: configMap.button_width,
					click: function () {
						showCreateGameRoomDialog();
					}
				},
				{id: 'quick-join', view: 'button', label: '빠른입장', width: configMap.button_width},
				{
					width: 409
				},
				app.v_theme_selector.getView()
			]
		};
	};

	return {
		initModule: initModule,
		getView: getView
	};
}());