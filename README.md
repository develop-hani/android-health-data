# 🏃‍♀️ 삼성 헬스 데이터 연동 연습
### 참고한 페이지
- [Accessing Samsung Health Data through Health Connect](https://developer.samsung.com/health/blog/en-us/2022/12/21/accessing-samsung-health-data-through-health-connect)
- [Reading Body Composition Data with Galaxy Watch via Health Connect API](https://developer.samsung.com/health/blog/en-us/2022/12/21/reading-body-composition-data-with-galaxy-watch-via-health-connect-api)
- [사용할 수 있는 데이터](https://developer.android.com/reference/androidx/health/connect/client/records/Record)

### 변경 사항
1. [여기](https://developer.samsung.com/health/blog/en-us/2022/12/21/reading-body-composition-data-with-galaxy-watch-via-health-connect-api) 하단에서 예제 어플리케이션 다운
2. [health_permissions.xml](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/res/layout/activity_main.xml)에 Nutrion 정보 읽기 권한 허용
3. [HealthConnectManage.kt](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/java/com/samsung/android/biaviewer/HealthConnectManager.kt)에서 가장 최근 식사의 열량 정보 수집
4. [activity_main.xml](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/res/values/health_permissions.xml)에 nutrition 정보를 제공하는 컴포넌트 추가
5. [MainActivity.kt](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/java/com/samsung/android/biaviewer/MainActivity.kt)에 (3)에서 수집한 정보를 (4)에서 추가한 컴포넌트에 바인딩