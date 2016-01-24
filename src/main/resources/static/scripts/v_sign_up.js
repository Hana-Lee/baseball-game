/**
 * @author Hana Lee
 * @since 2016-01-25 01:53
 */
/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app, webix, $$ */

app.v_sign_up = (function () {
	'use strict';

	var configMap = {
			width: 400
		}, getView, initModule,
		view;

	getView = function () {
		return view;
	};

	initModule = function (container) {
		view = webix.ui({
			id: 'login-container',
			type: 'space',
			css: 'login_container',
			container: container,
			borderless: true,
			rows: [{
				id: 'sign-up-form',
				view: 'form',
				width: configMap.width,
				hidden: false,
				scroll: false,
				elements: [
					{
						cols: [
							{
								width: 130,
								rows: [
									{
										id: 'avatar', view: 'uploader', label: '아바타사진 등록', name: 'avatar', required: false,
										accept: 'image/png, image/gif, image/jpg', link: 'avatar-list'
									},
									{ id:'avatar-list', height: 200, view:'list', scroll:false, type:'uploader' }
								]
							},{
								rows: [
									{
										id: 'nickname', view: 'text', label: '닉네임', name: 'nickname', required: true,
										on: {
											onAfterRender: function() {
												this.focus();
											}
										}
									},
									{
										id: 'email', view: 'text', type: 'email', label: '이메일', name: 'email', required: true
									},
									{
										id: 'password', view: 'text', type: 'password', label: '비밀번호', name: 'password', required: true
									},
									{
										id: 'sec-password', view: 'text', type: 'password', label: '비밀번호 확인', name: 'sec-password', required: true
									},
									{
										margin: 5,
										cols: [
											{
												view: 'button', value: '가입', type: 'form',
												click: function () {
													$$('sign-up-form').validate();
												}
											}
										]
									}
								]
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
		initModule: initModule,
		getView: getView
	};
}());