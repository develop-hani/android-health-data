# 🏃‍♀️ 삼성 헬스 데이터 연동 연습
## 참고한 페이지
- [Accessing Samsung Health Data through Health Connect](https://developer.samsung.com/health/blog/en-us/2022/12/21/accessing-samsung-health-data-through-health-connect)
- [Reading Body Composition Data with Galaxy Watch via Health Connect API](https://developer.samsung.com/health/blog/en-us/2022/12/21/reading-body-composition-data-with-galaxy-watch-via-health-connect-api)
- [사용할 수 있는 데이터](https://developer.android.com/reference/androidx/health/connect/client/records/Record)

## 변경 사항
### 1. 예제 애플리케이션 다운

- [Reading Body Composition Data with Galaxy Watch via Health Connect API](https://developer.samsung.com/health/blog/en-us/2022/12/21/reading-body-composition-data-with-galaxy-watch-via-health-connect-api)
- 이곳에 health connect api를 통한 애플리케이션을 구현하는 방법이 나와있다.
- 하단에서 예제 앱을 다운 받아서 영양 정보를 연동해보자.

### 2. Allow reading nutrition data

- [health_permissions.xml](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/res/layout/activity_main.xml)에서 데이터의 접근 권한을 추가
- [AndroidManifest.xml](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/AndroidManifest.xml)의 meta-data에서 접근 권한 배열을 읽어와서 등록하는 것을 확인할 수 있다.

### 3. Collect current nutrition data

- [HealthConnectManage.kt](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/java/com/samsung/android/biaviewer/HealthConnectManager.kt)에서 가장 최근 식사의 열량 정보 수집
- readNutritionRecord에서 최근 식사의 총 열량을 KCal 단위로 수집하는 것을 확인할 수 있다.

### 4. Create component

- [activity_main.xml](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/res/layout/activity_main.xml)에 nutrition 정보를 제공하는 컴포넌트 추가
- textBmrDesc아래 textCalaory 영역을 추가한다.
- 정적으로 제공되는 항목은 [strings.xml](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/res/values/strings.xml)에 등록하고 이를 불러 사용한다.

### 5. Bind data and component

- readAllData 함수에서
- [MainActivity.kt](https://github.com/develop-hani/android-health-data/blob/main/app/src/main/java/com/samsung/android/biaviewer/MainActivity.kt)의 readAllData() 함수에서 (3)에서 수집한 정보를 (4)에서 추가한 컴포넌트에 바인딩한다.