/**
 * @author Hana Lee
 * @since 2016-01-16 19:35
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix, $$ */

app.v_login = (function () {
	'use strict';

	var configMap = {
			width: 300
		},
		stateMap = {
			loginCompleted: false,
			logoutCompleted: true
		},
		initModule;

	initModule = function (container) {
		webix.ui({
			id: 'login-container',
			type: 'space',
			css: 'login_container',
			container: container,
			borderless: true,
			rows: [{
				id: 'login-form',
				view: 'form',
				width: configMap.width,
				hidden: false,
				scroll: false,
				elements: [
					{
						id: 'email', view: 'text', type: 'email', label: '이메일', name: 'email', required: true,
						on: {
							onAfterRender: function () {
								this.focus();
							}
						}
					},
					{
						id: 'password', view: 'text', type: 'password', label: '비밀번호', name: 'password', required: true
					},
					{
						margin: 5,
						cols: [
							{
								view: 'button', value: '로그인', type: 'form',
								click: function () {
									$$('login-form').validate();
								}
							},
							{
								view: 'button', value: '가입',
								click: function() {
									app.v_shell.showSignUp('main-container');
								}
							}
						]
					}
				],
				rules: {
					$obj: function (data) {
						var emailKey = 'email', passwordKey = 'password', message;
						if (!webix.rules.isNotEmpty(data[emailKey])) {
							message = '이메일 주소가 비어있습니다';
						} else if (!webix.rules.isEmail(data[emailKey])) {
							message = '이메일 주소가 잘못 입력되었습니다';
						} else if (!webix.rules.isNotEmpty(data[passwordKey])) {
							message = '패스워드가 비어있습니다';
						}

						if (message) {
							webix.message(message);
							return false;
						}
						return true;
					}
				}
			}]
		});
	};

	return {
		initModule: initModule
	};
}());