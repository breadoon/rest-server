[Breadoon Config]

config/runConfig.yml : REST API 서비스를 위한 전반적인 설정 정보를 가지고 있는다.

서비스 포트, JDBC 설정, 로깅, 실행 환경 동적 변수(개발/운영), CORS(Cross-Origin Resource Sharing)등에 대해 설정

config/built-in.yml : 내장 함수에 대한 정의.

사용자 정의 내장 함수를 추가할 수 있도록 구성된 파일이며 사용 방법에 따라 현재 두 가지 형태의 내장 함수를 직접 구현할 수 있도록 되어 있다.

config/restapi 디렉토리 : 이 디렉토리 하위에 버전별 rest api 디렉토리를 생성하고 API를 생성 관리할 수 있다.

자세한 REST API yaml 파일 구성 방법은 별도의 문서등을 통해 설명한다.

예로 "config/restapi" 하위에 "v1.0" 디렉토리를 생성한 후 API yaml 파일에 "/test/something"이라는 API를 생성한 경우

브라우저 또는 기타 도구에서 "http[s]://your-domain:port/v1.0/test/something"을 통해 호출이 가능하다.
