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
/*global $, app, webix, $$ */

app.v_game_room = (function () {
	'use strict';

	var configMap = {
			gen_num_count: 3
		}, stateMap = {
			selected_num: []
		}, initModule, view, getView;

	getView = function () {
		return view;
	};

	initModule = function () {
		view = [
			{template: '[1번방] 왕초보들 오세요', height: 45, type: 'header'},
			app.v_game_room_menu.getView(),
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
									width: 120, rows: [
									{template: '이하나님', height: 35, type: 'header'},
									{
										template: '<img src="images/character.gif" height="100%" width="100%">',
										width: 130
									}
								]
								},
								{
									id: 'game-pad-container',
									rows: [
										{
											id: 'game-pad',
											view: 'dataview',
											css: 'game_pad',
											type: {
												width: 112,
												height: 70,
												//templateStart: '<div item_id="#id#" class="game_pad">',
												//template: '<div class="webix_strong">#number#</div>',
												//templateEnd: '</div>'
												template: '<div class="overall">#number#</div>'
											},
											select: true,
											multiselect: true,
											scroll: false,
											hover: 'game_pad_hover',
											//xCount: 5,
											//yCount: 2,
											data: [
												{id: '0', number: 0},
												{id: '1', number: 1},
												{id: '2', number: 2},
												{id: '3', number: 3},
												{id: '4', number: 4},
												{id: '5', number: 5},
												{id: '6', number: 6},
												{id: '7', number: 7},
												{id: '8', number: 8},
												{id: '9', number: 9}
											],
											on: {
												'onItemClick': function (id, evt, el) {
													if (stateMap.selected_num.length === configMap.gen_num_count) {
														webix.alert({
															title: '경고',
															ok: '확인',
															text: '3개 이상 선택 할 수 없습니다'
														});
														return false;
													} else {
														stateMap.selected_num.push(id);

														setTimeout(function () {
															$$('game-pad').select(stateMap.selected_num);
														}, 0);
													}
												}
											}
										}, {
											id: 'number-submit',
											view: 'button',
											label: '제출'
										}
									]
								}
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
							app.v_main_chat.getView()
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