# 🛠️ 작업지시서: Android 알람 게임 앱 (Compose / Kotlin)

## 0. 목표

* **안드로이드 전용** 알람 앱을 개발한다.
* 알람이 울리면 사용자는 **게임(두더지 잡기 / 망치깨기) 성공 전까지 알람을 끌 수 없다.**
* 화면 수를 최소화하여 누구나 바로 사용할 수 있도록 한다.
* Android 13~15 환경에서 알람/알림/전체화면 동작이 **정책·권한 문제로 누락되지 않도록** 구현한다.

---

## 1. MVP 기능 범위 (기획)

### 1.1 기본 알람 기능

* 알람 생성 / 수정 / 삭제
* 시간 설정 (시 / 분)
* 반복 설정 (요일)
* 알람 이름(메모)
* 알람 ON / OFF 토글
* Snooze (간격 / 최대 횟수)
* 다음 알람까지 남은 시간 표시

### 1.2 게임 기반 알람 해제 (필수)

* **두더지 잡기 게임**
* **망치로 깨기 게임**
* 게임 성공 시에만 알람 종료
* 난이도 제공: 쉬움 / 보통 / 어려움 / 지옥

### 1.3 최소 화면 구성 (UX 원칙)

* Screen A: 알람 목록 (메인)
* Screen B: 알람 설정 (통합)
* Screen C: 알람 울림 + 게임 (전체화면)
* Screen D: 설정 (옵션)
* 모든 주요 기능은 **2단계 이내 접근**

---

## 2. 기술 스택

* Language: **Kotlin**
* UI: **Jetpack Compose**
* Architecture: **MVVM + Repository**
* Local DB: **Room**
* Async: Kotlin Coroutines + Flow
* Dependency Injection: Hilt (선택)
* Target SDK: 최신(API 35 권장)
* Min SDK: 26 이상 권장

---

## 3. 권한 및 OS 정책 고려사항

### 3.1 정확 알람 (Exact Alarm) – Android 14+

* `SCHEDULE_EXACT_ALARM` 권한은 기본 허용이 아님
* targetSdk 34+: `USE_EXACT_ALARM` 우선, 미허용 시 `SCHEDULE_EXACT_ALARM` 요청 및 설정 화면 딥링크 안내
* `AlarmManager.canScheduleExactAlarms()`로 가용성 확인 후 불가 시 inexact 스케줄 대안(리마인더) 안내
* 알람은 `setAlarmClock()` 우선 사용

### 3.2 알림 권한 – Android 13+

* `POST_NOTIFICATIONS` 런타임 권한 필수
* 앱 최초 실행 또는 알람 활성화 시 요청
* 거부 시 사용자 안내 제공

### 3.3 전체화면 알람

* 알람 발생 시 전체화면 Activity 표시
* 고우선순위 알림 + full-screen intent 사용, Android 14+ `USE_FULL_SCREEN_INTENT` 선언 및 사용자 이익 명시
* 알림 채널 중요도 HIGH 이상 생성, FSI 차단(DND/권한 거부) 시 대체 알림/화면 제공
* 광고·지속 노출 등 정책 위반 요소 금지

### 3.4 배터리 최적화 (Doze)

* 알람 누락 방지를 위해 배터리 최적화 예외 안내
* 필요 시 설정 화면 딥링크 제공

### 3.5 재부팅/프로세스 킬 복구

* `RECEIVE_BOOT_COMPLETED`/`LOCKED_BOOT_COMPLETED` 수신 후 모든 알람 재스케줄
* 프로세스 킬/백그라운드 제한 시에도 알람 사운드 유지: foreground service + `USAGE_ALARM` 오디오 속성, 진동 지속
* 알람/게임 Activity 백그라운드 이동 시 자동 복귀 또는 전체화면 재출력

---

## 4. 데이터 모델 (Room)

### 4.1 AlarmEntity

* id (Primary Key)
* hour, minute
* repeatDays (Bitmask 또는 List)
* enabled (Boolean)
* label (String)
* soundType / soundUri
* vibrate (Boolean)
* snoozeEnabled (Boolean)
* snoozeMinutes (Int)
* snoozeMaxCount (Int)
* gameType (MOLE / SMASH)
* difficulty (EASY / NORMAL / HARD / HELL)
* nextTriggerAt (UTC, Long) — DST/타임존 변화 대비 캐시
* createdAt / updatedAt

### 4.2 AlarmHistoryEntity (선택)

* alarmId
* firedAt
* dismissedAt
* snoozedCount
* gameSuccess

---

## 5. 알람 실행 상태 흐름

* SCHEDULED → FIRING → RINGING → PLAYING_GAME
* SNOOZED → 다시 SCHEDULED
* DISMISSED (게임 성공)

※ 게임 성공 전에는 알람 완전 종료 불가

### 5.1 회피 방지 / 예외 흐름

* 뒤로가기/홈/잠금키 입력 시에도 게임 복귀(전체화면 재출력), 태스크 제거 시 서비스 재실행
* 강제 종료/크래시 발생 시 Notification + tap-to-return으로 게임 재시작
* 화면 끄기 시에도 사운드/진동 지속, 기기 재부팅 시 알람/게임 재호출

---

## 6. 게임 스펙

### 6.1 두더지 잡기

| 난이도 | 동시 등장 | 목표 수 | 특징         |
| --- | ----- | ---- | ---------- |
| 쉬움  | 1     | 5    | 페널티 없음     |
| 보통  | 2     | 8    | 오타 시 1초 정지 |
| 어려움 | 3     | 12   | 가짜 두더지     |
| 지옥  | 4     | 20   | 실패 시 리셋    |

### 6.2 망치로 깨기

| 난이도 | 필요 타격 | 제한 시간 | 특징              |
| --- | ----- | ----- | --------------- |
| 쉬움  | 5     | 없음    | 약 진동            |
| 보통  | 10    | 20초   | 중 진동            |
| 어려움 | 15    | 15초   | 강 진동 + 회복       |
| 지옥  | 25    | 10초   | 강 진동 + 회복 + 페널티 |

---

## 7. UI 요구사항 (Compose)

### 7.1 알람 목록

* 알람 리스트 + ON/OFF 토글
* 다음 알람까지 남은 시간
* FAB(+) 알람 추가
* 스와이프 삭제

### 7.2 알람 설정

* 시간 피커
* 요일 선택
* Snooze 설정
* 게임 선택 (아이콘)
* 난이도 선택
* 저장 버튼 1개

### 7.3 알람 울림 + 게임

* 시스템 UI 최소화
* 사운드 + 진동 시작(난이도별 패턴/강도, 볼륨 점증 가능)
* 즉시 게임 시작
* 성공 시 즉시 종료
* 알림 채널: IMPORTANCE_HIGH 이상, 오디오 포커스 획득, FSI 차단 시 대체 알림/해제 버튼 제공
* 스누즈 버튼 노출(선택) 및 최대 횟수 소진 시 자동 해제 불가 안내

### 7.4 설정 (옵션)

* 기본 게임 / 난이도
* 기본 Snooze
* 권한 / 배터리 설정 바로가기

### 7.5 시간 경계 처리

* 과거 시각 입력 시 다음날로 자동 이동
* DST/타임존 변경 시 `nextTriggerAt` 재계산
* 반복 요일 비트마스크 해석 일관성 테스트 (주 시작 요일 명시)

---

## 8. 구현 작업 순서

1. 프로젝트 세팅 (Compose + MVVM)
2. Room(Entity / DAO / Repository)
3. 알람 목록 / 설정 화면
4. AlarmScheduler + BroadcastReceiver
5. 전체화면 알람 Activity
6. 게임 공통 인터페이스
7. 두더지 게임 구현
8. 망치깨기 게임 구현
9. 권한 처리 및 안내 UI
10. 재부팅 후 알람 복구
11. 배터리 최적화 안내

---

## 9. 완료 기준 (DoD)

* 알람 정확히 울림 (다수 알람 테스트)
* 잠금화면에서도 알람 표시
* 게임 성공 전 종료 불가
* Android 13/14/15 테스트 통과
* 권한 거부 시에도 크래시 없음
* 재부팅/프로세스 킬 후 알람 재스케줄 확인

## 10. 테스트 매트릭스

* Android 13/14/15 실제 기기: DND on/off, 알림 권한 허용/거부, 정확 알람 허용/거부, 배터리 최적화 on/off
* 반복 알람: DST 전후, 타임존 변경, 과거 시각 설정, 요일 비트마스크 전 조합
* 재부팅, 프로세스 강제종료, 태스크 스와이프 제거 후 알람/게임 재실행
* 전체화면 차단(FSI 거부) 시 알림 탭으로 게임 진입 가능 여부

---

✅ 본 문서는 AI 코딩 도구(Codex/Claude Code)에 그대로 전달하여 개발을 진행할 수 있는 작업지시서이다.
