# POV: Point Of View

> 본 프로젝트는 LG U+ 유레카 1기 백엔드 비대면 융합 프로젝트입니다. <br />
> 개발기간: 2024.11.12 ~ 2024.12.19 (6주)

![image](https://github.com/user-attachments/assets/f2a417c0-8bf7-4a1b-9f2f-86b4e022336f)
🔗 [Click! 배포된 POV 구경하러 가기](https://www.point-of-views.com/)

## _Intro._

영화의 다양한 관점을 공유하는 플랫폼 POV 의 주요 기능은 다음과 같습니다:

- **영화 리뷰 공유**
    - 관람한 영화에 대해 자신의 관점을 리뷰로 작성 및 조회할 수 있습니다.
    - 마음에 든 리뷰에는 `좋아요`를 등록할 수 있습니다.
- **맞춤 영화 추천**
    - 선호하는 영화 장르 기반으로 영화를 추천합니다.
    - 이때 추천은 TMDB 트렌딩 API 로 받은 영화 데이터를 참고하여 진행됩니다.
    - 조회된 영화 중 마음에 든 영화에는 `좋아요`를 등록할 수 있습니다.
- **클럽 내 영화 공유**
    - 비슷한 장르를 선호하는 사람들 또는 지인들과 영화를 공유하는 클럽에 `가입/초대` 할 수 있습니다.
    - 클럽은 `공개/비공개` 를 설정할 수 있습니다.
    - 클럽 내에 공유하고 싶은 영화는 `북마크`로 저장할 수 있습니다.
- **알림 서비스**
    - 새로운 리뷰가 등록되거나 클럽에 초대를 받을 경우 알림을 받을 수 있습니다.
    - 사용자는 마이페이지에서 `알림 활성화`를 설정할 수 있습니다.
- **시사회 응모 및 결제**
    - 일정 시간에 열리는 한정된 인원의 영화 시사회에 응모 및 결제할 수 있습니다.

<br />

## _Documents._

- [컨벤션 규칙 (Team | GitHub | Jira | TestCode | DTO | Method | Exception)](https://shinhm1.notion.site/13ce7e8fdd1280039f31f0e3da72d995?pvs=4)
- [API 명세서](https://shinhm1.notion.site/API-13de7e8fdd1280caa986dec01793f7ac?pvs=4)
- [Jira 를 활용한 프로젝트 관리](https://multicampusuplus.atlassian.net/jira/software/projects/POV/boards/6/timeline)
- [Swagger API 명세서 관리](https://www.point-of-views.com/api/docs)

<br />

## _Member._

> FE Developer

👨🏻‍💻 [POV FE 바로가기](https://github.com/ureca-final/pov-frontend)

<br />

> BE Developer

|                                                         **김영철 (팀장)**                                                          |                                                                **노지민**                                                                |                                                             **박시은**                                                              |                                                                    **이승희**                                                                     |
|:-----------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------:|
| [<img src="https://avatars.githubusercontent.com/u/43599437?v=4" height=150 width=150> <br/> @Kim](https://github.com/good4y) | [<img src="https://avatars.githubusercontent.com/u/166492522?v=4" height=150 width=150> <br/> @nohzimin](https://github.com/nohzimin) | [<img src="https://avatars.githubusercontent.com/u/62862307?v=4" height=150 width=150> <br/> @ssIIIn](https://github.com/ssIIIn0-0) | [<img src="https://avatars.githubusercontent.com/u/87460638?v=4" height=150 width=150> <br/> @leeseunghee00](https://github.com/leeseunghee00) |

<br />

> Role

| 이름  | 담당                                                                                                                                            |
|:---:|:----------------------------------------------------------------------------------------------------------------------------------------------|
| 김영철 | - TMDB API 영화 데이터 가져오기 <br/> - 관리자 영화 조회 API <br/> - 클럽 초대 및 조회 API <br/> - 최신 영화 동기화 배치 구현 <br/> - 맞춤 영화/리뷰 추천 배치 구현                         |
| 노지민 | - 영화 큐레이션 CRUD API <br/> - 클럽 CUD API <br/> - 클럽 북마크 API <br/> - 영화 좋아요 API <br/> - Full-text Search 를 적용한 검색 구현                              |
| 박시은 | - 회원가입/로그인 API <br/> - 리뷰 CUD API <br/> - 리뷰 좋아요 API <br/> - 리뷰 이미지 CRUD API <br/> - FCM 토큰을 이용한 알림 서비스 구현                                    |
| 이승희 | - 관리자 좋아요 관리 및 리뷰 숨김 API <br/> - 리뷰 조회 API <br/> - 회원 정보 수정 API <br/> - 시사회 CRUD API <br/> - 분산락을 적용한 응모 시스템 구현 <br/> - 토스페이먼츠를 이용한 결제 시스템 구현 |

<br />

## _Stack._

<img width="910" alt="image" src="https://github.com/user-attachments/assets/a9004588-6058-487e-ab1a-50a40e49a5f1" />

<br />

## _ER Diagram._

<img width="932" alt="image" src="https://github.com/user-attachments/assets/8308ad9e-e6df-4ecf-8c78-9f2454cf9d1b" />

<br />

## _SW Architecture._

![image](https://github.com/user-attachments/assets/cee974ff-cefb-4fb9-8b56-60d185b7571f)

<br />

## _Setup._

YAML 에 다음 설정이 필요합니다.

```yaml
spring:
  # MySQL 
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your-database
    username: your-username
    password: your-password

  # OAuth 
  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: your-client-id
            client-secret: your-client-secret

# AWS
cloud:
  aws:
    credentials:
      accessKey: your-access-key
      secretKey: your-secret-key
    s3:
      bucketName: your-bucket-name

# TMDB
TMDb:
  access: your-tmdb-access-key

# TOSS
toss:
  secret-key: your-toss-secret-key
```

<br />

## _Challenges & Solutions._

> **TMDB API 를 이용한 데이터 서빙** <br />
> 챌린저: [김영철](https://github.com/good4y)

- 문제1: TMDB 로부터 받은 데이터에 성인물이나 필수 필드가 누락된 데이터가 포함되어 저장되는 문제 발생
- 해결1
  - 트랜잭션이 겹치지 않도록 영화 테이블을 적절히 나누어 진행
  - 인기순으로 영화를 조회 후 성인물과 누락된 필드가 많이 분포되어 있는 하위 5% 를 제거  

<img width="500" alt="image" src="https://github.com/user-attachments/assets/6c13a1f9-d3f7-46b9-bf0a-2920a39bc77d" />

<br />
<br />

- 문제2: 영화를 포함한 약 5만 건의 데이터를 저장하는 데 약 1시간이 소요되는 문제 발생
- 해결2
    - 5개 스텝 중 영화 정보를 가져오는 첫 번째 스텝을 제외한 나머지 스텝은 비동기로 실행하여 배치 진행
    - 1초당 50회 요청 제한을 만족하기 위해 0.02초의 호출 리미터 적용

<img width="1375" alt="image" src="https://github.com/user-attachments/assets/83189267-9202-494f-a148-0c86d2b1f54c" />

<br />
<br />

> **Redisson 분산 락을 적용한 트랜잭션 충돌 해결** <br />
> 챌린저: [이승희](https://github.com/leeseunghee00)

- 문제: 선착순으로 이루어지는 응모 특성 상 수 천/만 명이 동시 접속 시 트랜잭션 충돌 발생
- 해결: Redisson 라이브러리를 이용한 분산 락을 적용하여 데이터 정합성을 보장

<img width="1242" alt="image" src="https://github.com/user-attachments/assets/c82a14c0-714c-464e-9383-118c5761fe1a" />

<br />
<br />

> **안정적인 알림 발송 전략** <br />
> 챌린저: [박시은](https://github.com/ssIIIn)
 
- 문제: FCM 단일 전송 방식으로 인한 알림 전송 실패에 대한 대책 필요
- 해결: 3번의 알림 발송 재시도와 지수백오프를 통해 시스템 부하를 분산하여 알림 성공률을 95% → 99% 까지 높임

<img width="996" alt="image" src="https://github.com/user-attachments/assets/f4458178-2287-495e-9402-96e19bbac7d9" />
