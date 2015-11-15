# 야구 게임
자바를 공부하며 만들어 보는 야구 게임

## 야구게임 설계

### 규칙

* 공격과 수비로 나눈다.
* 수비를 하는 사람이 0-9 까지의 숫자중 중복되지 않는 임의의 숫자 3개를 만든다.
* 공격자는 3자리의 숫자를 중복되지 않게 입력이 가능하며 3자리 이상, 이하로는 입력 할 수 없다.
* 수비자는 자신이 만든 숫자와 공격자가 입력한 숫자의 자리, 같은수를 판단한다.
* 수비자는 같은 자리의 같은 숫자일 경우 스트라이크, 다른 자리 같은 숫자일 경우 볼, 없는 숫자일경우 아웃을 말하며 결과를 합쳐서 최종적으로 알려준다.
* 아웃은 아무것도 맞는 숫자가 없을 경우에만 말한다. (1스트라이크 1볼 일 경우 1아웃은 말하지 않는다.)

```
예) 수비 (2, 4, 8) - 공격 (4, 5, 8) = 1스트라이크 1볼 (4는 자리는 다르지면 같은 수 이므로 1볼, 8은 같은 자리 같은 수 이므로 1스트라이크 이다)
예) 수비 (2, 4, 8) - 공격 (1, 2, 3) = 1볼
예) 수비 (2, 4, 8) - 공격 (1, 3, 7) = 3아웃
예) 수비 (2, 4, 8) - 공격 (2, 4, 9) = 2스트라이크
```

* 공격자가 입력할 수 있는 횟수는 게임 시작전 임의의 룰로 정할 수 있다. 보통 10회
* 정해진 횟수 안에 3스트라이크가 나오게 되면 공격자 승, 수비자 패
* 횟수를 넘어 서면 공격자 패, 수비사 승

### 기능 정의

* 사용자 입력 기능
* 0-9 사이 중복되지 않는 숫자 {3}자리 생성 기능
* 사용자 입력과 생성된 숫자 비교 기능
  * 입력값과 생성된 수의 자리 비교
  * 비교 결과 출력
* 사용자 입력 횟수 제한 기능
 * 기본 10회 (설정 가능)
* 입력 시간 제한 기능
 * 기본 120초 (설정 가능)
* 최종 결과 출력 기능
* 점수 기능
 * 기준 점수를 두고 차감하는 방식으로 한다
 * 횟수만큼 차감 (적은 횟수로 맞출 수록 점수는 높아진다)
 * 시간에 따라 차감 (빨리 입력 할 수록 점수는 높아진다)
* 점수 출력 후 사용자의 입력을 받아 기록하는 기능(오락실 게임이 끝나고 점수 기록하는 화면 참고)
* 게임 결과 기록 기능 (히스토리)
* 지난 결과 조회 기능
* **네트워크 (옵션)**
  * 생성된 숫자 하나를 여러명이서 대결하는 기능
  * 입력된 숫자와 결과는 입력한 사람만 볼 수 있는 기능
  * 먼저 맞춘 사람이 최종 승자
  * 결과 기록

### 클래스 설계
* 작성중...
