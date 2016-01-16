/**
 * Created by leehana on 2016. 1. 16..
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix */

app.v_login = (function () {
	'use strict';

	var configMap = {
			height: 400,
			width: 300
		},
		stateMap = {
			loginCompleted: false,
			logoutCompleted: true
		},
		initModule, show,
		loginWindow;

	show = function () {
		if (stateMap.loginCompleted) {
			alert('이미 로그인을 하였습니다');
		} else {
			loginWindow.show();
		}
	};

	initModule = function () {
		loginWindow = webix.ui({
			id: 'login-window',
			width: configMap.width,
			height: configMap.height,
			position: 'center',
			head: '로그인',
			view: 'window',
			modal: true,
			body: {
				id: 'login-form',
				view: 'form',
				elements: [
					{view: 'text', label: 'Username'},
					{view: 'text', type: 'password', label: 'Password'},
					{
						margin: 5, cols: [
						{view: 'button', value: '로그인', type: 'form'},
						{view: 'button', value: '가입', type: 'form'},
						{
							view: 'button', value: '닫기', on: {
							onItemClick: function () {
								loginWindow.hide();
							}
						}
						}
					]
					}
				]
			}
		});
	};

	return {
		initModule: initModule,
		show: show
	};
}());