REST API는 다음과 같은 구조를 가지고 실행한 동작을 정의하게 된다.

<img src="https://user-images.githubusercontent.com/120078630/207610644-1bd20495-a9bf-49dd-a83a-9092f13043d1.png"  width="70%">


[구성 요소 설명 ]

1. ROOT Element
   - ID(Identifier)는 별도의 directive를 가지지는 않으며 이후 정의할 구문을 대표하는 유일한 지시자가 된다. 이 대표 값이 중복되어 사용될 경우는 맨 마지막 값을 가지는 정의 내용이 API의 동작 내용으로 사용되므로 중복 여부에 유의하여야 한다. 
   
2. Child Element
   - desc : description(예, “first #1”)은 해당 RESI API에 대한 간단한 설명 정보를 기술한다.
   - pattern: pattern (예, “/testCal/{a_val}/{b_val}/{c_val}”)은 REST API의 구성 패턴을 정의한다. “{}”로 표현되는 부분은 pattern parameter로서 REST API Path내에 가변적인 영역을 지칭한다.
   - 
