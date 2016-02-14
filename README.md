# 야구 게임 [![Build Status](https://travis-ci.org/Hana-Lee/baseball-game.svg?branch=master)](https://travis-ci.org/Hana-Lee/baseball-game)
* 자바를 공부하며 만들어 보는 야구 게임 서버 및 클라이언트
* 서버는 Spring Boot 를 이용한 웹 서버 및 웹소켓을 이용한다
* 클라이언트는 SPA 방식으로 처리한다

## 스크린샷
* 로그인
![Login](https://raw.githubusercontent.com/Hana-Lee/baseball-game/master/src/main/resources/static/images/screenshot/ss_login.png)

* 가입
![Signup](https://github.com/Hana-Lee/baseball-game/blob/master/src/main/resources/static/images/screenshot/ss_signup.png?raw=true)

* 메인화면
![Main](https://github.com/Hana-Lee/baseball-game/blob/master/src/main/resources/static/images/screenshot/ss_main.png?raw=true)

* 게임룸
![GameRoom](https://github.com/Hana-Lee/baseball-game/blob/master/src/main/resources/static/images/screenshot/ss_gameroom.png?raw=true)

## 야구게임 설계

### 규칙

* 공격과 수비로 나눈다.
* 수비를 하는 사람이 0-9 까지의 숫자중 중복되지 않는 임의의 숫자 3개를 만든다.
* 공격자는 3자리의 숫자를 중복되지 않게 입력이 가능하며 3자리 이상, 이하로는 입력 할 수 없다.
* 수비자는 자신이 만든 숫자와 공격자가 입력한 숫자의 자리, 같은수를 판단한다.
* 수비자는 같은 자리의 같은 숫자일 경우 스트라이크, 다른 자리 같은 숫자일 경우 볼, 없는 숫자일경우 아웃을 말하며 결과를 합쳐서 최종적으로 알려준다.
* 아웃은 아무것도 맞는 숫자가 없을 경우에만 말한다. (1스트라이크 1볼 일 경우 1아웃은 말하지 않는다.)

```
예) 수비 (2, 4, 8)
	공격 (4, 5, 8) = 1스트라이크 1볼 (4는 자리는 다르지면 같은 수 이므로 1볼, 8은 같은 자리 같은 수 이므로 1스트라이크 이다)
	공격 (1, 2, 3) = 1볼
	공격 (1, 3, 7) = 3아웃
	공격 (2, 4, 9) = 2스트라이크
```

* 공격자가 입력할 수 있는 횟수는 게임 시작전 임의의 룰로 정할 수 있다. 기본 10회
	* 5단계로 있으며 선택가능하다
	```
	1. 1회
	2. 5회
	3. 10회
	4. 15회
	5. 20회
	```
	* 5단위로 입력이 가능하며 최소 1, 최대 20까지 입력이 가능하다
* 5변이상 잘못된 입력을 할 경우 게임 종료되며 점수는 0점이 된다.
* 정해진 횟수 안에 3스트라이크가 나오게 되면 공격자 승, 수비자 패
	* 횟수를 넘어 서면 공격자 패, 수비자 승
* 매번 게임이 시작될때 플레이어 리스트와 설정 정보와 전체 게임 진행 횟수를 보여준다.

### 게임룸 규칙
* 게임룸에는 최대 5명 최소 1명의 플레이어가 입장 가능하다.
* 게임룸 입장시 플레이어 리스트, 플레이어별 점수, 플레이어별 게임 진행 횟수, 설정 정보를 보여준다.
* 게임룸을 생성한 플레이어가 방장이 된다
* 방장이 게임룸을 나가는 경우 남은 플레이어중 랜덤하게 방장 권한 부여
* 방장은 게임의 설정을 바꿀 수 있다
* 게임룸에는 1명 혹은 0명의 수비자가 있을 수 있고 나머지는 공격자가 된다.
* 1명의 플레이어가 공격 역할일 경우 1인 게임도 가능하다
* 1명의 플레이어가 수비 역할일 경우 진행 불가
* 공격 역할은 게임룸당 1명만 존재 할 수 있다
* 게임 매 턴마다 게임의 턴횟수를 보여준다
* 게임을 맞춘 사람이 나타날 경우 등수, 점수, 아이디를 보여준다

### 점수 규칙
* 공격 (기본 10회 기준)
	* 참여 플레이어수에 20을 곱한 점수가 기준이 된다.
	* 5명중 1등 100점
	* 5명중 2등 90점
	* 5명중 3등 80점
	* 5명중 4등 70점
	* 5명중 5등 60점
	* ==
	* 4명중 1등 80점
	* 3명중 1등 60점
	* 2명중 1등 40점
	* 1명중 1등 20점
	* -10점씩 차감
	* 끝까지 못맞췄을 경우 5점
	* 동일한 횟수에 맞췄을 경우 더 빨리 입력하여 맞춘 사람이 높은 등수를 얻는다

* 수비 (기본 10회 기준)
	* 참여 플레이어수에 40을 곱한 점수가 기준이 된다.
	* 4명중 0명 맞춤 160점
	* 4명중 1명 맞춤 140점
	* 4명중 2명 맞춤 120점
	* 4명중 3명 맞춤 100점
	* ==
	* 3명중 0명 맞춤 120점
	* 3명중 1명 맞춤 100점
	* 3명중 2명 맞춤 80점
	* ==
	* 2명중 0명 맞춤 80점
	* 2명중 1명 맞춤 60점
	* -20 점씩 차감
	* 플레이어의 숫자와 상관없이 모든 플레이어가 맞춘경우 10점
* 모든 점수는 설정된 최대 추측 가능 횟수 만큼 아래의 공식대로 계산 된다.
	* 공격은 기본 횟수보다 적을 경우 점수가 1.5배씩 늘어난다 (▲) (5회 1회)
	* 공격은 기본 횟수보다 많을 경우 점수가 1.5배씩 줄어든다 (▼) (15회 20회)
	* 점수 예
	```
   	** 모든 공격 점수는 5명 기준 **
   	5회 공격 1등 150점 (1.5배 ▲)
   	1회 공격 1등 225점 (1.5배의 1.5배 ▲)

   	15회 공격 1등 67점 (1.5배 ▼) 100 / 1.5
   	20회 공격 1등 44점 (1.5배의 1.5배 ▼) 100 / 1.5 / 1.5

   	** 모든 수비 점수는 4명 기준 **
    5회 수비 0명 107점 (1.5배 ▼)
    1회 수비 0명 71점 (1.5배의 1.5배 ▼)

    15회 수비 0명 240점 (1.5배 ▲)
    20회 수비 0명 360점 (1.5배의 1.5배 ▲)
   	```
* 모든 점수는 설정된 생성 숫자의 갯수만큼 아래의 공식대로 계산 된다.
	* 공격은 기본갯수(3개) 보다 적을 경우 2배 줄어든다 (▼)
	* 공격은 기본갯수(3개) 보다 많을 경우 2배 늘어난다 (▲)
	* 수비는 기본갯수(3개) 보다 적을 경우 2배 늘어난다 (▲)
	* 수비는 기본갯수(3개) 보다 많을 경우 2배 줄어든다 (▼)
	* 점수 예
	```
	** 모든 공격 점수는 5명 기준 **
	2개 공격 1등 50점 (2배 ▼)
	4개 공격 1등 200점 (2배 ▲)
	5개 공격 1등 400점 (2배의 2배 ▲)

	** 모든 수비 점수는 4명 기준 **
	2개 수비 0명 320점 (2배 ▲)
	4개 수비 0명 80점 (2배 ▼)
	5개 수비 0명 40점 (2배의 2배 ▼)
	```
* 종합점수는 추측가능횟수 점수 + 생성숫자갯수 점수 로 계산된다.
	* 단 숫자를 못맞추고 게임이 끝났을경우 기본점수만으로 계산된다.
	* 점수는 소숫점 반올림으로 계산된다. (1.5 = 2, 1.4 = 1)
	```
	= 공격 점수는 5명 기준
	10회 추측 가능 + 4개 1등 : 100 + 200 = 300점
	15회 추측 가능 + 3개 1등 : 67 + 100 = 167점
	15회 추측 가능 + 2개 1등 : 67 + 50 = 117점

	= 수비 점수는 4명 기준
	10회 추측 가능 + 4개 0명 : 107 + 80 = 187점
	15회 추측 가능 + 3개 0명 : 240 + 160 = 400점
	15회 추측 가능 + 2개 0명 : 240 + 320 = 560점

	= 공격시 숫자를 못맞춘경우
	최소점수 5점을 기준으로하며 나머지 계산방법은 동일하다
	10회 추측 가능 + 4개 못맞춘경우 : 5 + 10 = 15점
	15회 추측 가능 + 3개 못맞춘경우 : 3 + 5 = 8점
	15회 추측 가능 + 2개 못맞춘경우 : 3 + 2 = 5점
	```

### 플레이어 레벨
* 플레이어는 게임을 진행 할 수록 레벨이 오르게 된다
* 레벨은 하나의 게임이 종료 될때의 결과로 경험치 계산을 한다
* 처음 레벨은 1레벨이며 상한선은 없다
* 레벨의 계산은 게임 종료시 플레이어의 점수로 계산된다
* 2레벨을 위해서는 100점이 필요하며 레벨마다 100점이 더해진 값이 필요하다
```
예)
2레벨 -> 3레벨 : 200점 필요
3레벨 -> 4레벨 : 300점 필요
99레벨 -> 100레벨 : 9900점 필요
100레벨 -> 101레벨 : 10000점 필요
``` 

### 플레이어 랭킹
* 플레이어의 랭킹은 점수를 기준으로 가장 높은 점수의 플레이어가 1등이 된다
* 점수가 중복될 경우 랭킹은 같다
* 플레이어의 처음 랭킹은 0으로 부터 시작한다

### 게임 전적 및 승률
* 게임을 진행 할수록 전적 및 승률이 계산 된다

### 게임 진행
1. 처음 게임 실행시 아이디 입력(로그인)
	1. 중복 아이디 체크
1. 게임룸 생성시
	1. 게임룸 생성 (이름 중복 가능)
	1. 역할 선택 (공격, 수비)
		1. 수비일때 다른 플레이어 입장 대기
		1. 공격일때 서버에서 만들어내는 숫자를 가지고 1인 플레이 가능
	1. 게임룸을 생성한 사람만이 게임룸의 설정(규칙)을 변경할 수 있다.
1. 게임룸 입장시
	1. 게임룸 선택
	1. 역할 선택 (공격, 수비)
		1. 이미 공격이 있을경우 선택 불가
1. 게임룸 탈퇴시
	1. 게임룸을 생성한 사람이 나갈 경우 게임룸에 존재하는 플레이어중 랜덤하게 방장 부여
	1. 모든 플레이어가 나갈 경우 게임룸은 삭제
1. 게임 진행
	1. 게임룸의 모든 플레이어가 준비를 선택하면 바로시작
		1. 수비일 경우 숫자 생성
		1. 준비가 완료 될때까지 대기(추후 대기시간 넣기)
		1. 준비가 모두 완료 되면 자동 시작
	1. 턴방식으로 게임 진행
		1. 모든 사용자가 정상적으로 입력을 마치면 다음 단계로 진행
	1. 모든 사용자가 맞추거나 입력 제한을 초과한 경우 현재의 게임 점수를 출력하고 종료

### 기능 정의

* 사용자 입력 기능
* 0-9 사이 중복되지 않는 숫자 {3}자리 생성 기능
	* 설정에 따라 3자리 이상 나올수 있음
* 사용자 입력과 생성된 숫자 비교 기능
	* 입력값과 생성된 수의 자리 비교
	* 비교 결과 출력
* 사용자 입력 횟수 제한 기능
	* 기본 10회 (설정 가능)
* 입력 시간 제한 기능 (추가 대기)
	* 기본 120초 (설정 가능)
* 최종 결과 출력 기능
* 점수 기능
* 점수 저장 기능
* 게임 결과 기록 기능 (히스토리)
* 지난 결과 조회 기능
* 게임룸 생성 기능
	* 게임룸을 생성한 플레이어가 방장이됨
	* 게임룸의 이름은 중복 가능
	* 게임룸별로 게임의 설정 변경 기능
* 아이디 생성 기능
	* 중복 아이디 체크
* 입력 시간 조절 기능
	* 25초동안 입력이 없을경우 횟수증가 후 다음 턴으로 이동
* 게임룸 내 역할 선택 기능
	* 수비, 공격으로 나뉜다
	* 수비는 한명만 존재 가능
* 게임룸은 최대 5명까지 인원 제한
* 게임룸에는 현재까지 진행된 게임의 횟수를 기록 및 출력
* 게임룸에서 역할 변경 기능
* 방장 변경 기능
* 게임룸 이름 변경 기능
* 플레이어 아이디 변경 기능
	* 중복 아이디 체크
* 생성된 숫자 하나를 여러명이서 대결하는 기능
* 입력된 숫자와 결과는 입력한 사람만 볼 수 있는 기능
* 먼저 맞춘 순서대로 등수가 매겨지고 점수 계산시 활용
* 게임의 진행횟수를 게임룸에 저장하는 기능
* 플레이어의 전적 및 승률, 랭킹은 모든 플레이어가 볼 수 있다(추후 공개 여부를 결정 할지도 고려)
