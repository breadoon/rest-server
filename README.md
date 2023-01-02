Breadoon REST API Engine 개요11

[ 필요성 ]
 
UI와 데이터를 분리하여 프로그램을 구성하거나, UI 필요 없이 정보 제공을 위해 데이터를 생산하는 경우는 RESTful한 API 서버를 구성하는 것은 필연적이라 할 수 있다. 
일반적으로 RESTful API를 구현하기 위해 node.js, Spring jersey, PHP등과 같은 프로그램을 이용하여 직접 코드를 작성하여 한다. 
하지만 대부분의 경우 프로그램 언어를 통해 직접 코드를 개발할 경우 다양한 배경 지식을 가진 개발자가 필요하게 되며 관련 개발에 소요되는 시간 또한 
상당한 시간이 필요하기 때문에 상대적으로 배경 지식이 적은 개발자라도 쉽게 SQL등을 이용하여 데이터 서비스를 할 수 있으며 시시 각각 변경이 요구되는 현대의 데이터 서비스 형태에 
좀 더 능동적으로 대처하기 위한 프레임웍이 필요하다.


[ 로우코드 솔루션 ]

신속한 애플리케이션 개발을 지원하기 위해 대두되고 있는 기술 중 하나가 로우코드(low-code) 솔루션들이다. 로우코드 솔루션은 필요한 기능을 간단한 명령을 조합하여 시스템을 만드는 개발 방법이다. 
쉽게 말하면, 복잡한 코딩 과정을 단순화해서 소프트웨어를 빠르게 개발 및 배포하도록 만든 일종의 개발 환경을 말하는 것이다. 
로우코드 개발 방법론은 다음의 장단점을 가질 수 있다.

* 로우코드(Low Code)의 장점
1) 프로그램 개발 시간 단축
2) 개발비를 절감
3) 오류 발생 빈도 최소화
4) 인력 확보가 비교적 쉽다.

* 로우코드의 단점
1) 자유도가 낮다.
2) 프로그래머의 창의성을 해칠 수 있다.
3) 보안에 취약할 수 있다.


[ Breadoon REST API Engine의 주요 특징 ]

1. Engine Level DB Transaction 처리를 통한 데이터 처리 무결성 보장
  - DB Query를 손쉽게 처리할 수 있도록 구성
  - Stored Procedure와 같이 앞선 Query의 결과를 기반으로 이후 동작을 제어하거나 UPDATE/DELETE/INSERT가 순차적으로 발생해야 하는 경우를 고려하여 
    여러 query를 하나의 REST API 호출을 통해 처리할 수 있는 방법론 제공 및 query 및 서버 프로그램 오류시 DB transaction에 대한 자동 rollback 기능 제공을 한다.

2. 직관성 있고 빠른 개발 지원
- REST API 정의 및 실행 내용을 하나의 구조에서 정의 가능하도록 지원(포맷 : yaml)
- 예) POST/GET HTTP method를 통해 DB 입력 및 조회 하는 예제

        C-analytics-add-ad-trace:
           desc: 다운로드 페이지 진입 시 추적 값을 등록한다.
           pattern: /analytics/ad
           method : POST
           bodyType: JSON
           needAuth: false
           actionType : sql
           sql:
              type: WRITE
              query: >-
                 INSERT INTO tb_analytics_ad
                 (uuid, from_source, from_medium, from_campaign, creation_time)
                 VALUES ( ?, ?, ?, ?, now(6))
              params :
                 - (!!string) s.UID
                 - (!!string) r.from_source
                 - (!!string) r.from_medium
                 - (!!string) r.from_campaign

        R-analytics-ad-trace:
           desc: 다운로드 페이지 진입 추적 값을 조회한다.
           pattern: /analytics/ad
           method : GET
           bodyType: URLENCODE
           needAuth: false
           actionType : sql
           sql:
              type: READ
              query: >-
                 SELECT *
                 FROM tb_analytics_ad
                 ORDER BY creation_time desc
                 LIMIT ?,?
              params :
                 - (!!string) r.fromIdx
                 - (!!string) r.dataCount


3. 다중 API 버전 지원
- 하위 버전의 API 서비스가 필요한 경우 동시에 여러 버전의 API를 서비스 할 수 있도록 구성 지원


4. MyBatis의 동적 Query 지원
- MyBatis 라이브러리를 이용하여 동적 Query 지원하여 다양한 조건식의 SQL 구문을 작성할 수 있도록 지원



[ 부가 기능 ]

REST API가 구성되었으면 이것을 테스트 해보고 유지하기 위한 도구가 필요한데 가장 대표적인 도구가 Swagger라 할 수 있다. breadoon에서는 yaml 파일별 테스트가 가능하도록 breadoon의 REST API yaml 파일을 swagger에서 인식할 수 있는 포맷으로 자동 변환해 주는 도구를 개발하여 API에 대한 공유 및 테스트가 가능하도록 하고 있다.

[ TO BE ]


1. 개발 저작 도구 및 통합 환경 제공
- 현재까지 breadoon의 REST API를 개발하기 위해서는 직접 Yaml 파일에 작성하도록 되어 있다. 다소 직관성이 떨어지기 때문에 이를 해결하기 위해 Jenkins의 blue ocean과 같은 UI를 가지는 개발 저작 도구를 통해 코드를 개발/유지/관리 할 수 있도록 지원한다.

