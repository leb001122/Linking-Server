## Linking-Server
- 프로젝트 진행 상황을 확인하고 관리할 수 있는 시스템
- 대상: 소규모 프로젝트 또는 조별과제를 진행하는 학생
- Spring Boot Server For Web & macOS Application
- Responsibility
    - Choi Hyemin: User, Project, Participant, Todo, Project Home (Todo Widget), Chatting
    - Lee Eunbin: Group, Page, Block, Annotation, Push Notification
    - Both: Security
- 주요 기능
    - 2명 이상의 사용자가 문서 동시 편집 가능 (CRDT or OT 사용X)
    - 할 일 페이지, 문서 페이지 등에서 다른 사용자가 등록/수정/삭제 것을 실시간으로 조회할 수 있음
    - 다른 팀원에게 푸시알림 or 메일 전송
    - 팀원 별 할일 관리
    - 프로젝트 별 채팅방
    - 기타 : 사용자, 프로젝트, 그룹, 페이지, 주석 등등
- 비기능적 요구사항
    - 사용자의 페이지 요청에 대한 결과가 3초 이내에 응답되어야 한다.
    - 할 일, 페이지, 문서(그룹,페이지) 목록, 알림함에서 새로 고침 하지 않아도 변경사항이 반영되어야 한다.
    - 2명 이상의 사용자가 같은 페이지를 조회하고 있을 때, 다른 사용자가 페이지의 내용을 수정하면 다른 사용자들이 1분 이내로
       편집된 내용을 확인할 수 있어야 한다. 또한, 2명 이상의 사용자가 동시에 페이지를 편집할 수 있어야 한다.
- 사용 기술
    - WebSocket
        - 페이지 동시 편집, 채팅에 사용.
        - stomp를 사용하지 않고 순수 websocket으로 구현.
        - 문제: 연결 후 약 1분 동안 요청/응답이 없으면 연결 끊김.
        - 해결: schedular를 이용해여 45초마다 ping msg 전송 
    - Server Send Event
        - 문서 목록, 페이지, 할 일, 알림함에 사용
        - 연결된 sseEmitter 객체를 서버의 메모리에 저장 (추후 redis 이용할 예정)
        - 문제: websocket과 달리 클라이언트에서 close해도 서버에서 감지하기 어려움
        - 해결: 클라이언트에서 close 시 close api를 호출하면 서버에서 해당 객체를 삭제
   - FCM Server
       - 푸시 알림에 사용
       - 플랫폼 (web, macos app)에 종속되지 않고 알림을 전송하기 위해 FCM server를 이용함
- 구현 설명
   - 동시편집
     1) 사용자가 글자 입력
     2) 클라이언트가 새로 입력된 글자를 포함한 전체 문자열을 서버로 전송
     3) 서버에서 이전 문자열과 새로 받은 문자열을 비교하여 insert/update, 문자열이 삽입된 위치, 삽입된 문자열을 구함
     4) 다른 클라이언트에게 전송
     5) 클라이언트는 받은 데이터를 토대로 적절한 위치에 문자열 삽입

      
