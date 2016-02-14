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
		},
		stateMap = {
			container: ''
		}, initModule;

	initModule = function (container) {
		stateMap.container = container;
		webix.ui({
			id: 'sign-up-container',
			type: 'space',
			css: 'sign_up_container',
			container: stateMap.container,
			borderless: true,
			rows: [{
				type: 'header',
				template: '회원가입'
			}, {
				id: 'sign-up-form',
				view: 'form',
				width: configMap.width,
				hidden: false,
				scroll: false,
				elements: [
					{
						cols: [
							{
								rows: [
									{
										id: 'avatar',
										view: 'uploader',
										label: '아바타사진 등록',
										name: 'avatar',
										required: false,
										accept: 'image/png, image/gif, image/jpg',
										link: 'avatar-preview',
										click: function (id, evt) {
											evt.preventDefault();

											webix.alert({
												title: '정보',
												ok: '확인',
												text: '아바타 설정은 준비중입니다'
											});
										}
									},
									{
										id: 'avatar-preview',
										template: '<img src="images/blank_character_2.gif" height="100%" width="100%">',
										width: 130,
										height: 173
									}
								]
							},
							{
								width: 10
							},
							{
								rows: [
									{
										height: 25
									},
									{
										id: 'email',
										view: 'text',
										type: 'email',
										label: '이메일',
										name: 'email',
										required: true,
										on: {
											onAfterRender: function () {
												$$('email').focus();
											}
										}
									},
									{
										height: 10
									},
									{
										id: 'nickname', view: 'text', label: '닉네임', name: 'nickname', required: true
									},
									{
										height: 10
									},
									{
										id: 'password',
										view: 'text',
										type: 'password',
										label: '비번',
										name: 'password',
										required: true
									},
									{
										height: 10
									},
									{
										id: 'matching-password',
										view: 'text',
										type: 'password',
										label: '비번 확인',
										name: 'matchingPassword',
										required: true
									},
									{
										height: 10
									},
									{
										margin: 5,
										cols: [
											{
												view: 'button', value: '가입', type: 'form', hotkey: 'enter',
												click: function () {
													var result = $$('sign-up-form').validate();

													if (result) {
														webix.ajax().headers({
															'Content-type': 'application/json'
														}).post('player',
															JSON.stringify(
																$$('sign-up-form').getValues()
															),
															{
																error: function(text/*, data, XmlHttpRequest */){
																	var textJson = JSON.parse(text);
																	webix.alert({
																		title: '오류',
																		ok: '확인',
																		text: textJson.message
																	});
																},
																success: function(/* text, data, XmlHttpRequest */){
																	webix.alert({
																		title: '정보',
																		ok: '확인',
																		text: '가입이 완료 되었습니다'
																	});
																	app.v_shell.showLogin();
																}
															}
														);
													}
												}
											},
											{
												view: 'button', value: '취소', type: 'danger',
												click: function () {
													app.v_shell.showLogin();
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
						var nicknameKey = 'nickname', emailKey = 'email',
							passwordKey = 'password', secPasswordKey = 'matchingPassword', message;

						if (!webix.rules.isNotEmpty(data[nicknameKey])) {
							message = '닉네임이 비어있습니다';
						} else if (!webix.rules.isNotEmpty(data[emailKey])) {
							message = '이메일 주소가 비어있습니다';
						} else if (!webix.rules.isEmail(data[emailKey])) {
							message = '이메일 주소가 잘못 입력되었습니다';
						} else if (!webix.rules.isNotEmpty(data[passwordKey])) {
							message = '패스워드가 비어있습니다';
						} else if (data[passwordKey] !== data[secPasswordKey]) {
							//message = '패스워드가 다릅니다<br/>다시 확인해주세요';
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