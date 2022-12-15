breadoon REST API는 다음과 같은 구조를 가지고 실행한 동작을 정의하게 된다.

<img src="https://user-images.githubusercontent.com/120078630/207610644-1bd20495-a9bf-49dd-a83a-9092f13043d1.png"  width="70%">


[구성 요소 설명 ]

1. ROOT Element

   - ID(Identifier)는 별도의 directive를 가지지는 않으며 이후 정의할 구문을 대표하는 유일한 지시자가 된다. 이 대표 값이 중복되어 사용될 경우는 맨 마지막 값을 가지는 정의 내용이 API의 동작 내용으로 사용되므로 중복 여부에 유의하여야 한다. 
   
2. Child Element

   - desc : description(예, “first #1”)은 해당 RESI API에 대한 간단한 설명 정보를 기술한다.
   
   - pattern: pattern (예, “/testCal/{a_val}/{b_val}/{c_val}”)은 REST API의 구성 패턴을 정의한다. “{}”로 표현되는 부분은 pattern parameter로서 REST API Path내에 가변적인 영역을 지칭한다.
   
   - method : method(예, “GET”)는 RESTful한 URL 구성을 위해 사용하는 “POST”, “GET”, “PUT”, “DELETE”등과 같은 Http Method를 지칭하며 일반적으로 대표적인 4가지 타입의 method는 DB 관련 동작을 지칭하는 CRUD(Create, Read, Update, Delete)와 각각 대응될 수 있다. 
     여기에 추가적으로 breadoon REST API에서만 사용되는 “INLINE”이 추가되었다. 
     "INLINE" method는 독자적으로 호출되지 않으며 동일한 pattern을 가지는 GET 메소드중 다양한 검색등을 지원하기 위한 기능등을 설계할 때 사용할 수 있으며 ROOT Element인 ID값의 앞부분을 동일하게 설계하고 그 뒤에 "-q:"로 시작하는 추가적인 명명(ex:aaa)이 이루어진 경우이면서 "INLINE"의 경우는 GET 메소드의 파라미터로 "subFunc=q:aaa"를 추가적으로 호출하면 동일한 패턴을 가지더라고 다른 파라미터 타입을 가지는 API를 호출할 수 있다. 
     
   - needAuth : REST API는 크게 public/private/privacy의 3단계 level로 보안 설정이 가능하다. 일반적으로 public인 경우는 로그인을 하지 않아도 접근이 가능한 영역의 데이터를 의미하고 private는 로그인 보안이 필요한 정보의 접근 제어하기 위한 것이며 privacy는 자신의 정보를 조회하거나 수정하는 작업에 사용된다. 여기서 needAuth는 public과 private를 결정하기 위해 사용한다. privacy는 호출되는 REST API의 소유를 판단하여야 하기 때문에 별도의 기능으로 제어한다. needAuth가 “false”로 설정된 경우는 로그인 여부등을 판단하지 않고 결과를 반환하게 되며 “true”로 설정된 경우는 로그인 여부등의 보안 정보를 확인한 후 결과를 반환하게 된다.

   - bodyType : bodyType(예, “JSON”)은 HTTP Request parameter의 형식을 지정한다. 현재 지원하고 있는 타입은 “JSON”과 “URLENCODE”의 두 형식이다. JSON의 경우는 breadoon의 ROOT JSON 구조의 하부에 다양한 형태의 JSON을 사용할 수 있으며 URLENCODE의 경우는 일반적인 URL에 호출에 사용되는 파라미터 구조를 사용하면 된다. breadoon에서 사용하는 JSON 호출구조는 별도의 문서를 통해 정리하도록 한다.
   
   - actionType : actionType은 실제 실행되는 action의 종류를 선택하게 된다. 현재 “sql”, “builtIn”, "actionBlock" 세가지를 선택할 수 있으며 이 설정에 따라 실제 REST API business 동작을 수행하게 되는 내용에 대해 기술하게 된다.
     “sql”의 경우는 DB를 통해 SQL Query를 실행하는 경우, “builtIn”의 경우는 시스템 제공 함수 또는 개발자 구현 함수를 호출하는 경우,  "actionBlock"의 경우는 위의 “sql”과 “builtIn”을 혼합하거나 여러번의 호출을 통해 구성할 때 사용한다.
     

3. Action Element

   - 
