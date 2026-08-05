# FPT-web (팀 서비스)

이 프로젝트는 Spring Boot 기반의 웹 애플리케이션으로, FPT 내부용 팀 서비스(계정, 공지, 캘린더/일정, 투표 등)를 제공합니다. 서버 측 템플릿(Thymeleaf)으로 구성된 웹 UI와 일부 REST API를 포함합니다.

## 체크리스트 (수정/검토 순서)
- 프로젝트 목적과 주요 기능 정리
- 빌드 및 실행 방법 (Windows PowerShell 기준)
- 필수 설정(환경 변수, secret 파일, 업로드 디렉터리) 안내
- 주요 페이지/엔드포인트 목록 제공
- 테스트 및 배포 참고

## 주요 기능
- 사용자 인증(로그인/회원가입/프로필/비밀번호 재설정)
- 공지사항(등록/목록/상세)
- 캘린더(일정 등록/목록/사용자 일정)
- 투표(투표 생성/목록/상세)
- 파일 업로드 (로컬 디스크에 저장)

## 프로젝트 구조(요약)
- `build.gradle`, Gradle Wrapper (`gradlew.bat`)으로 빌드
- Java 소스: `src/main/java/com/side_fpt/team_service`
- 템플릿(Thymeleaf): `src/main/resources/templates`
- 정적 리소스: `src/main/resources/static` 및 외부 업로드 디렉터리(`D:/uploads/`)
- 설정 파일: `src/main/resources/application.properties`, `src/main/resources/secrect.properties`

## 필수 사전 준비
1. JDK 17+ 설치 (프로젝트는 Gradle Java toolchain에서 Java 17을 사용하도록 설정되어 있습니다)
2. 프로젝트 루트에서 Gradle Wrapper 사용 가능 (Windows): `.\gradlew.bat`
3. 업로드 디렉터리 생성: 기본 설정은 `D:\uploads\` 입니다. (컨트롤러에서 파일을 이 경로로 읽고 씁니다.)
4. Jasypt 복호화 비밀번호: 데이터베이스 및 민감 정보가 `ENC(...)` 형태로 암호화되어 있습니다. 애플리케이션 실행 시 Jasypt 암호(예: `JASYPT_ENCRYPTOR_PASSWORD`)를 제공해야 합니다. 예:

   - 환경 변수 설정 (PowerShell):
	 ```powershell
	 $env:JASYPT_ENCRYPTOR_PASSWORD="yourJasyptPassword"
	 .\gradlew.bat bootRun
	 ```

   - 또는 JVM 옵션으로 전달:
	 ```powershell
	 .\gradlew.bat bootRun -Djasypt.encryptor.password=yourJasyptPassword
	 ```

5. `secrect.properties` 파일에 메일 설정(`spring.mail.username`, `spring.mail.password`)이 포함되어 있습니다. 비밀번호는 민감 정보이므로 안전하게 관리하세요.

## 빌드 및 실행 (Windows PowerShell)
- 빌드: `.\gradlew.bat build`
- 애플리케이션 실행 (개발): `.\gradlew.bat bootRun` (Jasypt 비밀번호 필요)
- 실행 가능한 JAR로 실행:
  1. `.\gradlew.bat bootJar` 또는 `.\gradlew.bat build`
  2. `java -jar build\libs\<프로젝트-이름>-<버전>.jar -Djasypt.encryptor.password=yourJasyptPassword`
- 테스트 실행: `.\gradlew.bat test`

기본 포트: `8080` (설정: `server.port=8080`)

## 로컬에서 실행 — 상세 가이드 (Windows PowerShell)

아래 단계는 로컬 개발 환경에서 애플리케이션을 실행하기 위한 권장 절차입니다. 필요에 따라 일부 단계를 건너뛸 수 있습니다.

1) JDK 17 설치 및 환경 변수 설정
   - 권장 배포판: Eclipse Temurin(Adoptium), Azul Zulu 등
   - 설치 후 PowerShell 세션에 임시로 설정(영구 설정은 시스템 환경 변수에 추가):
```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

2) PostgreSQL (또는 사용하려는 DB) 설치 및 데이터베이스 생성
   - 로컬에 PostgreSQL을 설치(기본 포트 5432 사용 권장)
   - psql로 접속하여 데이터베이스와 사용자 생성 예시:
```powershell
psql -U postgres
# psql 내부에서 실행
CREATE DATABASE user_service_db;
CREATE USER team_user WITH ENCRYPTED PASSWORD 'your_db_password';
GRANT ALL PRIVILEGES ON DATABASE user_service_db TO team_user;
\q
```

3) 업로드 디렉터리 생성
```powershell
New-Item -ItemType Directory -Path 'D:\uploads' -Force
```

4) 애플리케이션 설정(두 가지 방법)

- 방법 A — Jasypt 복호화 비밀번호로 기존 암호화된 설정 사용
  - `application.properties`에 이미 암호화된 `spring.datasource.*` 값(ENC(...))이 존재합니다. 이 값을 사용하려면 Jasypt 비밀번호가 필요합니다.
  - PowerShell 예시:
```powershell
$env:JASYPT_ENCRYPTOR_PASSWORD = 'yourJasyptPassword'
.\gradlew.bat bootRun
```
  - 또는 JAR 실행 시 JVM 옵션으로 전달:
```powershell
java -jar build\libs\<artifact>.jar -Djasypt.encryptor.password=yourJasyptPassword
```

- 방법 B — 환경 변수 또는 외부 프로퍼티로 데이터베이스 설정 재정의
  - `application.properties`의 ENC(...) 대신 환경 변수로 직접 재정의할 수 있습니다.
  - PowerShell 예시:
```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/user_service_db'
$env:SPRING_DATASOURCE_USERNAME = 'team_user'
$env:SPRING_DATASOURCE_PASSWORD = 'your_db_password'
.\gradlew.bat bootRun
```
  - 또는 `src/main/resources/application-local.properties` 파일을 만들어 실행 시 `--spring.config.location=classpath:/,classpath:/application-local.properties` 등으로 로드하게 할 수 있습니다.

5) `secrect.properties` (메일 설정)
  - `src/main/resources/secrect.properties`에 `spring.mail.username`과 `spring.mail.password`가 있습니다. 실제로 이메일 기능을 사용하려면 유효한 SMTP 계정을 넣으세요.
  - 운영 환경에서는 이 파일을 저장소에 커밋하지 말고 환경변수나 Vault를 사용하세요.

6) 애플리케이션 실행
  - 개발 모드(실행 로그를 바로 확인):
```powershell
.\gradlew.bat bootRun
```
  - 빌드 후 JAR로 실행:
```powershell
.\gradlew.bat bootJar
java -jar build\libs\<프로젝트-이름>-<버전>.jar -Djasypt.encryptor.password=yourJasyptPassword
```

7) 확인
  - 브라우저에서 http://localhost:8080 접속
  - 주요 페이지: `/`, `/login`, `/vote/voteBoard`, `/announce/announcement`, `/calendar/calendar` 등

8) 테스트 실행
```powershell
.\gradlew.bat test
```

문제 해결 팁
- Jasypt 복호화 실패: `JASYPT_ENCRYPTOR_PASSWORD` 값이 올바른지 확인하세요.
- DB 연결 실패: PostgreSQL이 실행 중인지, 포트/방화벽, JDBC URL, 사용자/비밀번호가 정확한지 확인하세요.
- 포트 충돌: 8080 포트를 이미 사용 중이면 `application.properties`에서 `server.port`를 변경하거나 JVM 옵션으로 오버라이드하세요.

추가로 원하시면 Windows용 `run.ps1` 실행 스크립트(환경 변수 설정, 업로드 디렉터리 생성 포함)와 Dockerfile/Docker Compose 예시를 만들어 드리겠습니다.

## 주요 엔드포인트 / 페이지
- 홈: `GET /` -> `templates/home.html`
- 로그인/회원가입: `/login`, `/register`, `/user/profile`, `/user/change-password`, `/user/forgot-password`
- 투표
  - UI: `/vote/voteBoard`, `/vote/voteDetail?voteId=...`, `/vote/addVote`
  - REST API: `/api/v1/votes` (예: 전체 투표 조회 등)
- 공지사항: `/announce/announcement`, `/announce/addAnnouncement`, `/announce/detailAnnouncement`
- 캘린더: `/calendar/calendar`, `/calendar/addSchedule`, `/calendar/userSchedule/{userId}`
- 파일 업로드 API: `POST /api/v1/upload` (기본 저장소: `D:/uploads/`)

> 참고: 컨트롤러 및 템플릿에서 경로 및 리소스 사용 방식을 확인하여 필요한 경로/권한을 맞춰주세요.

## 설정 파일 (중요 항목)
- `src/main/resources/application.properties`
  - 데이터베이스 설정(`spring.datasource.url/username/password`)이 Jasypt로 암호화되어 있을 수 있습니다.
  - `spring.config.import=optional:secrect.properties` 를 사용해 `secrect.properties` 파일을 로드합니다.
  - 정적 리소스 및 업로드 경로: `file.upload-dir=D:/uploads/`, `spring.web.resources.static-locations` 등에 설정되어 있습니다.

## 테스트
- 간단한 Spring Boot 컨텍스트 테스트가 포함되어 있습니다. 실행: `.\gradlew.bat test`

## 배포 및 운영 팁
- 프로덕션에서는 `secrect.properties`와 Jasypt 비밀번호를 안전한 Vault 혹은 환경 변수로 관리하세요.
- 업로드 디렉터리의 권한과 디스크 공간을 모니터링하세요.
- 이메일 기능(비밀번호 재설정 등)을 사용하려면 `secrect.properties`에 올바른 SMTP 계정이 필요합니다.

## 기여
- 내부용 프로젝트로 보이며, 추가 기능이나 버그 수정은 코드 스타일과 패키지 구조를 따라 `src/main/java`에 PR을 통해 반영하세요.

## 참고 문서
- 빌드 파일: `build.gradle`
- 실행 엔트리: `com.side_fpt.team_service.TeamServiceApplication` (메인 클래스)
- 설정: `src/main/resources/application.properties`, `src/main/resources/secrect.properties`

---
필요하시면 이 README에 더 자세한 설치 가이드(예: Dockerfile, 실행 예시 스크린샷, API 예제 응답 등)를 추가해드리겠습니다.
