<div align="center">

# A-Law 메인 서버

> 임대차 계약서 AI 분석 플랫폼 — 인증 · CRUD · 비동기 파이프라인 · 실시간 스트리밍

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?logo=mongodb&logoColor=white)](https://www.mongodb.com)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)](https://redis.io)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-AMQP-FF6600?logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com)
[![AWS S3](https://img.shields.io/badge/AWS_S3-232F3E?logo=amazonaws&logoColor=white)](https://aws.amazon.com/s3)
[![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)](https://www.docker.com)

</div>

---

**A-Law 메인 서버**는 임대차 계약서 AI 분석 플랫폼의 핵심 백엔드입니다.  
사용자 인증, 계약서 관리, AI 분석 요청 오케스트레이션, 실시간 결과 스트리밍을 담당하며, AI 전담 서버(FastAPI, 포트 8001)와 RabbitMQ를 통해 비동기로 연동됩니다.

---

## 목차

- [주요 기능](#주요-기능)
- [시스템 아키텍처](#시스템-아키텍처)
- [기술 스택과 선택 이유](#기술-스택과-선택-이유)
- [데이터 설계](#데이터-설계)
- [핵심 플로우](#핵심-플로우)
- [해결한 핵심 문제들](#해결한-핵심-문제들)
- [API 엔드포인트](#api-엔드포인트)
- [보안 아키텍처](#보안-아키텍처)
- [FastAPI 연동](#fastapi-연동)
- [모니터링 및 로깅](#모니터링-및-로깅)
- [개발 환경 설정](#개발-환경-설정)
- [배포](#배포)

---

## 주요 기능

### 카카오 소셜 로그인 및 JWT 인증
OAuth2 프로토콜로 카카오 계정과 연동하고, 액세스 토큰(7일)과 리프레시 토큰(14일)을 발급합니다. 리프레시 토큰은 Redis에 저장해 만료 시 자동 삭제하고, 이후 재발급 요청에 대응합니다.

### 계약서 업로드 및 OCR 파이프라인
사용자가 업로드한 계약서 이미지를 AWS S3에 저장한 뒤, FastAPI의 OCR 엔드포인트를 동기 호출로 텍스트를 추출합니다. 추출된 텍스트와 단어별 좌표 정보는 MongoDB에 저장하고, AI 분석 요청은 RabbitMQ 메시지로 비동기 발행합니다.

### 실시간 분석 결과 스트리밍 (SSE)
AI 분석이 진행되는 동안 클라이언트는 Server-Sent Events(SSE)로 결과를 실시간 수신합니다. 분석 완료 전에 접속이 끊겼다가 재접속해도 저장된 이벤트를 리플레이해 결과 유실을 방지합니다.

### 독소조항 위험 분석 결과 조회
FastAPI가 RabbitMQ로 발행한 분석 결과(조항별 위험 점수, 근거 법령, 전체 위험 수준)를 수신해 MongoDB에 저장하고 클라이언트에 구조화된 형태로 제공합니다.

### 음성 팩트체크
계약 협의 녹음 파일을 업로드하면 FastAPI의 STT + 팩트체크 파이프라인으로 처리됩니다. 계약서와 연계된 경우 발화 내용과 계약 조항의 불일치를 탐지하고, 단독 분석도 지원합니다.

### 법률 용어 쉬운말 설명 및 챗봇
복잡한 법률 문장을 FastAPI의 RAG 기반으로 쉬운 말로 바꾸는 API, 임대차 관련 질문에 답하는 챗봇 API를 프록시 방식으로 제공합니다.

---

## 시스템 아키텍처

```
┌──────────────────────────────────────────────────────────┐
│                   프론트엔드 (React)                       │
└──────────────────────────┬───────────────────────────────┘
                           │ HTTPS
                           ▼
┌──────────────────────────────────────────────────────────┐
│          Spring Boot 메인 서버 (포트 8080)                 │
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐  │
│  │ 인증/JWT │  │ 계약서   │  │  음성    │  │챗봇    │  │
│  │ OAuth2   │  │ 관리     │  │ 팩트체크  │  │쉬운말  │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────┘  │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │   비동기 파이프라인 (RabbitMQ Producer/Consumer)    │  │
│  │   실시간 스트리밍 (SSE Emitter Manager)             │  │
│  └────────────────────────────────────────────────────┘  │
└──────┬──────────┬──────────┬──────────┬──────────────────┘
       │          │          │          │
       ▼          ▼          ▼          ▼
  PostgreSQL   MongoDB     Redis    RabbitMQ
  (User,       (분석결과,  (JWT리프  (계약/음성
   Contract,    OCR결과,   레시토큰,  분석 큐)
   AnalysisJob) 챗봇)      캐시)
       │
       ▼
    AWS S3
  (계약서 이미지,
   음성 파일)
       │
       ▼ REST / RabbitMQ
┌──────────────────────┐
│  FastAPI AI 서버     │
│  (포트 8001)         │
│  OCR · RAG · STT     │
│  위험탐지 · 챗봇     │
└──────────────────────┘
```

---

## 기술 스택과 선택 이유

### Spring Boot 3.5.6 + Java 21 — 엔터프라이즈 기반 선택

**선택 이유**: 사용자 인증, 계약서 CRUD, 비즈니스 로직, 외부 서비스 연동을 하나의 서버에서 다루어야 하는 상황에서, Spring Boot의 생태계는 이 모든 요구를 기존 검증된 라이브러리로 해결한다. JPA, Security, AMQP, WebFlux, Redis, MongoDB 스타터가 모두 통합되어 있어 설정 비용이 최소화된다.

Java 21을 선택한 이유는 Virtual Thread(Project Loom)의 안정화다. 블로킹 I/O 작업(JDBC, 파일 입출력)을 가상 스레드에서 실행하면 스레드 풀 크기에 제약받지 않는 높은 동시성을 확보할 수 있다. OCR 동기 호출(최대 90초)처럼 대기 시간이 긴 작업에서 특히 효과적이다.

### PostgreSQL — 사용자/계약서 관계 데이터

**선택 이유**: 사용자(User), 계약서(Contract), 분석 작업(AnalysisJob) 간에는 명확한 외래 키 관계가 있다. "사용자 ID별 계약서 목록", "계약서 ID로 분석 작업 상태 조회" 같은 쿼리는 JOIN 연산이 자연스러운 관계형 모델에 적합하다.

NoSQL로 처리하면 애플리케이션 레이어에서 관계를 직접 관리해야 하고, 트랜잭션(계약서 저장 + 분석 작업 생성을 원자적으로 처리) 보장도 어려워진다. `@Transactional`로 두 엔티티를 하나의 트랜잭션에서 처리하는 것이 핵심이었다.

### MongoDB Atlas — 비정형 AI 분석 결과 저장

**선택 이유**: AI 분석 결과의 구조가 유동적이다. 독소조항 분석 결과는 `clauseResults[]` 배열이고, 각 항목에 `legalReference`, `reasoningSummary`, `category` 등 여러 필드가 포함된다. OCR 결과는 단어별 좌표 배열이 수천 항목에 달할 수 있다.

이를 PostgreSQL에 저장하려면 여러 테이블과 복잡한 JOIN이 필요하고, 새 필드 추가마다 스키마 마이그레이션이 발생한다. MongoDB는 FastAPI가 보내는 JSON 구조 그대로 저장하고 `s3_key`, `job_id`에 인덱스를 걸어 빠르게 조회한다. Atlas는 별도 서버 운영 없이 TLS 인증서 기반 클라우드 연결을 지원한다.

### Redis — JWT 리프레시 토큰과 캐시의 이중 역할

**선택 이유**: 두 가지 독립적인 문제를 Redis 하나로 해결했다.

**리프레시 토큰 저장**: `@TimeToLive` 어노테이션으로 14일 TTL을 설정하면 별도 배치 삭제 작업 없이 토큰이 자동 만료된다. `userId`로 인덱싱해 "이 사용자의 리프레시 토큰을 모두 무효화(로그아웃)"가 단일 명령으로 처리된다.

**API 캐싱**: 계약서 목록(`contracts-list:{userId}`)과 상세(`contracts-detail:{contractId}`)를 10분 TTL로 캐싱한다. `@Cacheable`과 `@CacheEvict`로 계약서 수정·삭제 시 관련 캐시가 자동 삭제된다.

### RabbitMQ — AI 처리의 비동기 분리

**선택 이유**: OCR 이후 AI 분석(요약 + 독소조항 위험 탐지)은 평균 10~20초가 소요된다. 사용자가 파일을 업로드하고 20초를 기다리는 HTTP 동기 방식은 사용자 경험상 허용하기 어렵다.

RabbitMQ 메시지 큐를 도입하면, Spring Boot는 분석 요청을 큐에 발행하고 즉시 "접수됨" 응답을 반환한다. FastAPI는 큐에서 순서대로 처리하고 결과를 역방향 큐로 발행한다. 메시지에 `persistent=true`를 설정해 RabbitMQ 재시작 시에도 미처리 작업이 유실되지 않는다.

DLX(Dead Letter Exchange)를 구성해 처리 실패 메시지를 DLQ로 이동시킨다. 이로써 재시도 정책(FastAPI 측 3회 재시도)과 실패 분석이 독립적으로 운영된다.

### Spring Security + OAuth2 + JWT — 두 가지 인증 방식의 조합

**선택 이유**: 사용자가 서비스 최초 접속 시에는 카카오 OAuth2로 소셜 로그인을 하고, 이후 API 호출에서는 매번 OAuth2 흐름을 거칠 수 없다. JWT 액세스 토큰으로 이후 요청을 처리하는 하이브리드 방식이 필요했다.

Spring Security의 OAuth2 Client는 카카오와의 인가 코드 교환, 사용자 정보 조회를 자동으로 처리한다. `OAuth2SuccessHandler`에서 JWT를 발급하고 쿠키에 설정하는 흐름으로 두 방식을 자연스럽게 연결했다. `OncePerRequestFilter`로 구현한 `JwtAuthenticationFilter`가 이후 모든 요청의 토큰을 검증한다.

### Spring WebFlux WebClient — FastAPI 비동기 HTTP 호출

**선택 이유**: OCR 호출은 최대 90초까지 소요된다. `RestTemplate`(동기 블로킹)을 사용하면 해당 스레드가 90초 동안 대기 상태로 묶인다. WebClient의 비동기 논블로킹 방식으로 대기 중에도 다른 요청을 처리할 수 있다.

`WebClient`를 두 개로 분리했다. OCR 전용 클라이언트는 읽기 타임아웃 90초, 일반 AI 클라이언트(쉬운말 요약, 챗봇)는 30초로 설정했다. 각 클라이언트에 요청/응답 로깅 필터를 추가해 FastAPI 연동 문제를 추적한다.

### Server-Sent Events (SSE) — 폴링 없는 실시간 스트리밍

**선택 이유**: AI 분석 결과가 준비되는 시점을 클라이언트가 알 방법이 없다. 가장 단순한 해법은 클라이언트가 주기적으로 결과를 요청하는 폴링이지만, 1초 간격 폴링은 사용자 10명 기준으로 분당 600회 불필요한 API 호출이 발생한다.

SSE는 클라이언트가 한 번 연결하면 서버가 이벤트를 능동적으로 푸시한다. WebSocket 대비 단방향이고 HTTP 기반이라 방화벽/프록시 환경에서 안정적이다. `SseEmitterManager`가 `jobId`별 Emitter를 관리하고, RabbitMQ 결과 수신 즉시 해당 jobId 구독자 전체에게 이벤트를 브로드캐스트한다.

### AWS SDK v2 + S3 — 계약서·음성 파일 저장소

**선택 이유**: 계약서 이미지와 음성 파일은 데이터베이스에 저장하기에 적합하지 않다. 파일 크기가 크고(최대 100MB), FastAPI가 S3 키만으로 직접 접근해야 한다. S3는 URL 기반으로 두 서버가 동일 파일을 독립적으로 접근할 수 있는 공유 저장소 역할을 한다.

SDK v2를 선택한 이유는 비동기 클라이언트(`S3AsyncClient`) 지원과 새로운 스트리밍 API다. 버킷 버전관리는 `S3BucketVersioningInitializer`가 애플리케이션 시작 시 자동으로 활성화한다.

### MapStruct — Entity ↔ DTO 변환

**선택 이유**: 컨트롤러, 서비스, 메시지 계층 간 데이터 변환에서 수동으로 `new DTO(entity.getField1(), entity.getField2(), ...)` 패턴을 반복하면 필드 추가 시 누락이 발생하기 쉽다. MapStruct는 컴파일 타임에 매핑 코드를 생성해 런타임 오버헤드 없이 안전한 변환을 보장한다.

---

## 데이터 설계

### PostgreSQL 엔티티 관계

```
User (1) ─── (N) Contract (1) ─── (N) AnalysisJob
  │                  │
  │                  └─── (N) VoiceRecord ────┐
  │                                           │
  └─── (N) VoiceRecord (계약서 없는 단독)      │
                                              ▼
                                     (MongoDB 저장)
                                     VoiceFactCheckDocument
```

**User**

| 필드 | 타입 | 설명 |
|------|------|------|
| userId | Long (PK) | 식별자 |
| name | String | 카카오 계정 이름 |
| role | Enum | USER / ADMIN |
| provider | Enum | KAKAO |
| providerId | String | 카카오 고유 ID |
| isDelete | Enum | Y / N (소프트 삭제) |

**Contract**

| 필드 | 타입 | 설명 |
|------|------|------|
| contractId | Long (PK) | 식별자 |
| user | FK → User | 소유 사용자 |
| title | String | 계약서 제목 |
| fileUrl | String | S3 URL |
| analysisId | String | AnalysisJob jobId |
| contractType | Enum | 임대차, 근로, 구매 등 |
| status | Enum | PENDING → PROCESSING → COMPLETED / FAILED |
| bookmark | Boolean | 북마크 여부 |
| userSaved | Boolean | 사용자 확인 저장 여부 |
| rawText | TEXT | OCR 추출 원문 |

**AnalysisJob**

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 식별자 |
| jobId | String (unique) | UUID, RabbitMQ 추적 키 |
| contractId | Long (FK) | 대상 계약서 |
| status | Enum | PENDING → IN_PROGRESS → COMPLETED / FAILED |
| processingTimeMs | Long | AI 처리 소요 시간 |
| errorMessage | String | 실패 시 오류 내용 |

### MongoDB 도큐먼트

**ContractAnalysisDocument** — 독소조항 분석 결과

```javascript
{
  contractId: Long,
  s3Key: String,       // 인덱스
  jobId: String,       // 인덱스
  summaryTitle: String,
  summaryText: String,
  keyTerms: [String],
  totalClauses: Number,
  riskCount: Number,
  cautionCount: Number,
  safetyCount: Number,
  overallRiskScore: Number,
  overallRiskLevel: String,  // HIGH / MEDIUM / LOW
  clauseResults: [{
    clauseTitle: String,
    clauseContent: String,
    riskLevel: String,
    legalReference: String,
    relatedWork: String,
    reasoningSummary: String,
    category: String,
    score: Number
  }],
  processingTimeMs: Number
}
```

**OcrResultDocument** — OCR 추출 결과

```javascript
{
  s3Key: String,         // 인덱스
  imageUrl: String,
  fullText: String,
  markdown: String,      // 마크다운 구조화 텍스트
  words: [{
    text: String,
    bbox: [x, y, width, height],
    confidence: Double
  }],
  warnings: [String]
}
```

**VoiceFactCheckDocument** — 음성 팩트체크 결과

```javascript
{
  voiceRecordId: Long,  // 인덱스
  contractId: Long,
  jobId: String,
  transcript: String,
  factCheckItems: [{
    claim: String,           // 발화 내용
    contractContent: String, // 계약서 조항
    isMatch: Boolean         // 일치 여부
  }],
  processingTimeMs: Number
}
```

### Redis 키 구조

```
리프레시 토큰: refresh:token:{userId}  TTL: 14일
계약서 목록 캐시: contracts-list:{userId}  TTL: 10분
계약서 상세 캐시: contracts-detail:{contractId}  TTL: 10분
```

---

## 핵심 플로우

### 계약서 분석 전체 흐름

```
[1] 사용자 → POST /api/v1/contracts/ocr (파일 업로드)
    │
    ▼
[2] 파일 검증 (형식, 크기 100MB 제한)
    │
    ▼
[3] S3 업로드 → s3Key 확보
    │           경로: contracts/{UUID}_{filename}
    ▼
[4] FastAPI OCR 동기 호출 (90초 타임아웃)
    │  POST /ai/contracts/ocr
    │  응답: fullText, words(좌표), maskedImageUrl
    ▼
[5] OcrResultDocument → MongoDB 저장
    │
    ▼
[6] Contract 엔티티 → PostgreSQL 저장
    │  status: PENDING
    │  rawText: OCR fullText
    ▼
[7] AnalysisJob 생성 → PostgreSQL 저장
    │  jobId: UUID
    │  status: PENDING
    ▼
[8] RabbitMQ 메시지 발행 (비동기)
    │  Exchange: contract-analysis-ex
    │  payload: {jobId, contractId, s3Key, userId}
    ▼
[9] 클라이언트에 jobId 반환 (즉시)

--- 이후 비동기 ---

[10] FastAPI Consumer → 메시지 수신
     │  OCR 텍스트 조회 → 요약 + 위험 분석 병렬 실행
     ▼
[11] FastAPI → RabbitMQ 결과 발행
     │  Exchange: contract.analysis.result
     ▼
[12] Spring ContractAnalysisConsumer → 수신
     │
     ├─ AnalysisJob 상태 → COMPLETED
     ├─ Contract 상태 → COMPLETED
     ├─ ContractAnalysisDocument → MongoDB 저장
     └─ SSE 이벤트 브로드캐스트 (jobId 구독자 전체)
         ├─ summary_result
         ├─ risk_analysis
         ├─ clause_results
         └─ completion

--- 클라이언트 SSE 구독 (병렬) ---

[9-a] GET /api/v1/contracts/analysis/{jobId}/stream
      ├─ 분석 완료 전 접속: Emitter 등록 → 이벤트 대기
      └─ 분석 완료 후 접속: 저장된 이벤트 리플레이
```

### 음성 팩트체크 흐름

```
[1] POST /api/v1/voice/records (음성 파일 + 선택적 contractId)
    │
    ▼
[2] S3 업로드 → voice/{UUID}_{filename}
    │
    ▼
[3] VoiceRecord 저장
    │  contractId 있음: VoiceRecord.of(contract, ...)   → 계약서 연계 분석
    │  contractId 없음: VoiceRecord.ofVoiceOnly(...)    → 단독 음성 분석
    ▼
[4] RabbitMQ 발행
    │  Exchange: voice-analysis-ex
    ▼
[5] FastAPI: STT + 팩트체크 + 결과 발행
    │
    ▼
[6] VoiceFactCheckConsumer → 수신
    ├─ VoiceFactCheckDocument → MongoDB
    ├─ VoiceRecord status → COMPLETED
    └─ SSE 이벤트 → 클라이언트
```

---

## 해결한 핵심 문제들

### 문제 1: AI 분석 완료 전 SSE 접속 시 이벤트 유실

**상황**: 클라이언트가 분석 요청 직후 SSE를 구독하면 분석이 아직 진행 중이므로 이벤트가 없다. 반대로 분석이 먼저 완료되고 클라이언트가 늦게 접속하면 이미 발행된 이벤트를 받을 방법이 없었다.

**해결**: `SseEmitterManager`에 이벤트 저장 기능을 추가했다. RabbitMQ에서 결과를 수신할 때 이벤트를 `Map<jobId, List<SseEvent>>`에 저장해둔다. 클라이언트가 SSE를 구독할 때 이미 저장된 이벤트가 있으면 즉시 리플레이한다. 이후 도착하는 이벤트는 실시간으로 전송한다. 5분 타임아웃으로 완료된 세션의 Emitter를 정리한다.

### 문제 2: OCR 90초 호출 중 스레드 고갈

**상황**: Spring Boot 기본 스레드 풀(200개)에서 OCR 동기 호출이 90초씩 대기하면, 동시 업로드 200건이 발생하면 스레드가 전부 점유되어 다른 요청(계약서 목록 조회 등)을 처리하지 못한다.

**해결**: 두 가지 방법을 병행했다.
- **WebClient 도입**: OCR 호출을 `WebClient`의 논블로킹 방식으로 변환했다. I/O 대기 중에 스레드를 반환하고 응답이 오면 다시 할당받는다.
- **Java 21 Virtual Thread**: `spring.threads.virtual.enabled=true` 설정으로 블로킹 작업도 가상 스레드에서 실행한다. 수백만 개의 가상 스레드가 OS 스레드 소수를 공유하므로 스레드 고갈 문제가 근본적으로 해결된다.

### 문제 3: RabbitMQ 재시작 시 미처리 메시지 유실

**상황**: AI 분석 요청이 큐에 쌓인 상태에서 RabbitMQ 서버가 재시작되면 인메모리 큐의 메시지가 모두 사라진다. 사용자는 "분석 요청됨" 상태에서 영구 대기하게 된다.

**해결**: 두 가지 설정을 적용했다.
- **메시지 Persistent**: `MessageProperties.setPersistent(true)`로 메시지를 RabbitMQ 디스크에 기록한다.
- **Queue Durable**: `QueueBuilder.durable()`로 큐 자체를 재시작 후에도 유지한다.

또한 DLX(Dead Letter Exchange)를 구성했다. 처리 실패한 메시지는 `contract-analysis-queue.dlq`로 이동해 수동 검토 또는 재처리가 가능하다.

### 문제 4: 캐시 불일치로 인한 오래된 데이터 반환

**상황**: 사용자가 계약서 제목을 수정하거나 계약서를 삭제한 뒤, 10분 TTL이 남은 Redis 캐시에서 이전 데이터가 반환되는 문제가 발생했다.

**해결**: `@CacheEvict`를 수정/삭제 서비스 메서드에 추가해 데이터 변경 시 관련 캐시를 즉시 무효화했다.

```java
// 계약서 수정 시 목록 캐시와 상세 캐시 동시 삭제
@CacheEvict(cacheNames = {"contracts-list", "contracts-detail"},
            key = "#contractId")
public void updateContract(Long contractId, ...) { ... }
```

캐시 에러 발생 시 `CacheErrorHandler`가 DB 폴백으로 전환해 서비스 중단을 방지한다.

### 문제 5: FastAPI 호출 실패 시 사용자에게 오류 전파

**상황**: OCR 호출이 네트워크 오류나 FastAPI 서버 문제로 실패했을 때, 일반적인 500 오류만 반환하면 사용자는 무슨 문제인지 알 수 없다.

**해결**: `FastApiException`을 별도 예외 클래스로 정의하고 `GlobalExceptionHandler`에서 502 Bad Gateway로 변환해 "AI 서버 연동 문제"임을 명확히 전달한다. WebClient의 `4xxClientError`, `5xxServerError`, 타임아웃 각각에 대해 `FastApiException`을 발생시킨다. 로깅 필터가 요청 URI, 응답 상태, 응답 본문을 기록해 디버깅을 지원한다.

### 문제 6: OAuth2 로그인 후 토큰 전달 방법

**상황**: OAuth2 콜백은 서버 간 리다이렉트이므로, 카카오로부터 콜백을 받은 뒤 JWT를 프론트엔드에 전달하는 방법이 명확하지 않았다. JSON 응답은 리다이렉트 후 URL 이동이 이루어진 상태라 받을 수 없다.

**해결**: `OAuth2SuccessHandler`에서 두 단계로 처리한다.
1. 액세스 토큰을 `Authorization` 응답 헤더와 `HttpOnly` 쿠키 두 경로로 모두 설정한다.
2. 리프레시 토큰은 `HttpOnly + Secure(프로덕션)` 쿠키로만 설정해 XSS로 탈취되지 않도록 한다.
3. 프론트엔드 URL로 리다이렉트하면, 브라우저가 쿠키를 보존한 채 이동해 이후 API 요청에 자동으로 토큰이 포함된다.

### 문제 7: Spring Boot와 FastAPI 간 필드명 불일치 (snake_case vs camelCase)

**상황**: FastAPI(Python)는 `job_id`, `risk_analysis` 같은 snake_case를 사용하고, Spring Boot는 `jobId`, `riskAnalysis` 같은 camelCase를 사용한다. RabbitMQ 메시지에서 필드명이 맞지 않아 역직렬화가 실패했다.

**해결**: Spring Boot DTO에 Jackson `@JsonProperty`와 `@JsonAlias`를 명시적으로 적용해 두 표기법을 동시에 허용한다.

```java
public class AnalysisResultMessage {
    @JsonProperty("jobId")
    @JsonAlias("job_id")
    private String jobId;

    @JsonProperty("riskAnalysis")
    @JsonAlias("risk_analysis")
    private RiskAnalysis riskAnalysis;
}
```

FastAPI 측에서도 `model_config = ConfigDict(populate_by_name=True)`와 camelCase alias를 설정해 양방향으로 호환된다.

---

## API 엔드포인트

### 인증

| 메서드 | 경로 | 기능 | 인증 |
|--------|------|------|------|
| GET | `/login/oauth2/authorization/kakao` | 카카오 로그인 시작 | 불필요 |
| DELETE | `/api/v1/auth` | 로그아웃 | 필요 |
| PUT | `/api/v1/auth` | 토큰 재발급 | 불필요 |
| GET | `/api/v1/auth` | 토큰 유효성 확인 | 불필요 |

### 계약서

| 메서드 | 경로 | 기능 | 인증 |
|--------|------|------|------|
| POST | `/api/v1/contracts/ocr` | 파일 업로드 + OCR + 분석 시작 | 필요 |
| GET | `/api/v1/contracts` | 내 계약서 목록 | 필요 |
| GET | `/api/v1/contracts/{contractId}` | 계약서 상세 | 필요 |
| PUT | `/api/v1/contracts/{contractId}` | 계약서 수정 | 필요 |
| DELETE | `/api/v1/contracts/{contractId}` | 계약서 삭제 | 필요 |
| POST | `/api/v1/contracts/{contractId}/bookmark` | 북마크 토글 | 필요 |
| GET | `/api/v1/contracts/analysis/{jobId}` | 분석 결과 조회 | 필요 |
| GET | `/api/v1/contracts/analysis/{jobId}/stream` | 분석 결과 SSE 스트림 | 필요 |
| POST | `/api/v1/contracts/explanation` | 법률 용어 쉬운말 요약 | 필요 |

### 음성

| 메서드 | 경로 | 기능 | 인증 |
|--------|------|------|------|
| POST | `/api/v1/voice/records` | 음성 파일 업로드 | 필요 |
| GET | `/api/v1/voice/records` | 음성 목록 | 필요 |
| DELETE | `/api/v1/voice/records/{voiceRecordId}` | 음성 삭제 | 필요 |
| GET | `/api/v1/voice/fact-checks/{jobId}` | 팩트체크 결과 조회 | 필요 |
| GET | `/api/v1/voice/fact-checks/{jobId}/stream` | 팩트체크 SSE 스트림 | 필요 |

### 챗봇

| 메서드 | 경로 | 기능 | 인증 |
|--------|------|------|------|
| POST | `/api/v1/chat` | 법률 챗봇 대화 | 필요 |

### 공통 응답 포맷

```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": { ... }
}
```

오류 응답:

```json
{
  "status": 400,
  "code": "BAD_REQUEST",
  "message": "파일 형식이 올바르지 않습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 보안 아키텍처

### OAuth2 + JWT 하이브리드 인증 흐름

```
[1] 사용자 → /login/oauth2/authorization/kakao
    │
    ▼
[2] 카카오 인가 코드 발급 → Callback (/login/oauth2/code/kakao)
    │
    ▼
[3] OAuth2UserService.loadUser()
    ├─ 카카오 providerId로 기존 사용자 검색
    └─ 없으면 User.of()로 자동 가입
    │
    ▼
[4] OAuth2SuccessHandler
    ├─ JwtProvider.createAccessToken(userId, role) → 7일
    ├─ JwtProvider.createRefreshToken(userId) → Redis 저장, 14일 TTL
    ├─ 쿠키 설정 (HttpOnly, Secure)
    └─ 프론트엔드 URL로 리다이렉트

[5] 이후 API 요청: Authorization: Bearer <accessToken>
    │
    ▼
[6] JwtAuthenticationFilter
    ├─ 헤더에서 토큰 추출
    ├─ JwtValidator.validateAccessToken()
    │   ├─ 서명 검증 (HS256)
    │   └─ 만료 확인
    └─ SecurityContext에 인증 정보 설정

[7] 토큰 만료 시
    ├─ PUT /api/v1/auth (쿠키의 리프레시 토큰 전송)
    ├─ Redis에서 리프레시 토큰 검증
    └─ 새 액세스 토큰 발급
```

### 권한 체계

| 경로 패턴 | 필요 권한 |
|----------|----------|
| `/api/v1/auth/**` | 누구나 |
| `/login/oauth2/**` | 누구나 |
| `/swagger-ui/**` | 누구나 |
| `/api/v1/contracts/**` | USER 이상 |
| `/api/v1/voice/**` | USER 이상 |
| `/api/v1/admin/**` | ADMIN만 |

---

## FastAPI 연동

### RabbitMQ 메시지 계약

**Spring → FastAPI (계약서 분석 요청)**

```
Exchange:    contract-analysis-ex
Queue:       contract-analysis-queue
Routing Key: contract.analyze
TTL:         24시간

payload:
{
  "jobId": "uuid",
  "contractId": 123,
  "s3Key": "contracts/uuid_filename.jpg",
  "userId": 456
}
```

**FastAPI → Spring (분석 결과)**

```
Exchange:    contract.analysis.result
Queue:       ai.result.queue
Routing Key: ai.result

payload:
{
  "jobId": "uuid",
  "contractId": 123,
  "status": "COMPLETED",
  "summary": {
    "title": "표준임대차계약서",
    "summaryText": "...",
    "keyTerms": ["보증금", "월세"],
    "duration": "2024-01-01 ~ 2026-01-01"
  },
  "riskAnalysis": {
    "totalClauses": 12,
    "riskCount": 2,
    "cautionCount": 4,
    "safetyCount": 6,
    "overallRiskScore": 45,
    "overallRiskLevel": "MEDIUM",
    "clauseResults": [...]
  },
  "processingTimeMs": 15000
}
```

> **중요**: Exchange/Queue 이름, 메시지 필드명 변경 시 이 레포의 `RabbitMQConfig.java`, `ContractAnalysisMessage.java`와 FastAPI의 `app/core/config.py`, `app/schemas/contract_analysis_dto.py`를 반드시 동시에 수정해야 한다.

### REST 직접 호출 (동기)

| 클라이언트 | FastAPI 엔드포인트 | 타임아웃 | 용도 |
|-----------|------------------|---------|------|
| OCRClient | POST /ai/contracts/ocr | 90초 | 이미지 → 텍스트 |
| AIClient | POST /ai/contracts/explain/term | 30초 | 용어 쉬운말 요약 |
| AIClient | POST /ai/chat | 30초 | 챗봇 대화 |
| VoiceAnalysisClient | POST /ai/voice/analyze-s3 | 30초 | 음성 분석 |

---

## 모니터링 및 로깅

### 로깅 전략

**HTTP 요청/응답 로그** (`LogFilter`): 모든 HTTP 요청의 메서드, URI, 응답 상태, 처리 시간을 기록한다. `./logs/springboot.log`에 저장하고 로그 로테이션을 적용한다.

**AOP 서비스 로그** (`LogAspect`): `@Service`, `@Controller`, `@Repository` 어노테이션이 붙은 클래스의 public 메서드 호출마다 입력값, 반환값, 실행 시간을 기록한다. 예외 발생 시 스택 트레이스를 포함해 로깅한다.

### Prometheus 메트릭

`/actuator/prometheus` 엔드포인트에서 수집된다.

| 메트릭 | 설명 |
|--------|------|
| `http_requests_total` | HTTP 요청 수 (경로, 메서드, 상태 레이블) |
| `http_request_duration_seconds` | 요청 처리 시간 히스토그램 |
| `spring_data_repository_invocations_total` | JPA 쿼리 호출 수 |
| `rabbitmq_consumed_total` | RabbitMQ 메시지 수신 수 |
| `jvm_memory_used_bytes` | JVM 힙/메타스페이스 사용량 |

---

## 개발 환경 설정

### 필수 환경

- Java 21+
- Docker (로컬 인프라 실행)
- Gradle 8+


### 서버 실행

```bash
# Gradle 빌드 후 실행
./gradlew bootRun --args='--spring.profiles.active=local'

# 또는 JAR 직접 실행
./gradlew bootJar
java -jar build/libs/alaw-*.jar --spring.profiles.active=local
```


## 배포

### Docker (멀티스테이지 빌드)

```bash
docker build -t alaw-main-server .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=... \
  -e DB_URL=... \
  alaw-main-server
```

**빌드 구조**: Stage 1(빌더)에서 Gradle 의존성 캐싱 후 JAR를 빌드하고, Stage 2(런타임)에서 `eclipse-temurin:21-jre-alpine` 기반으로 JAR만 복사해 최소한의 이미지 크기를 유지한다.

### CI/CD (GitHub Actions)

```
push to main/develop
    │
    ▼
[1] 테스트 (test 프로필, H2 인메모리)
    │
    ▼
[2] Docker 이미지 빌드 & DockerHub 푸시
    │  태그: latest, {branch}, {commit SHA}
    ▼
[3] A-Law-Cloud 레포에 deploy-backend 이벤트 발행
    └─ 운영 서버 자동 배포 트리거
```

### 배포 환경별 설정

| 항목 | 로컬 | 프로덕션 |
|------|------|---------|
| PostgreSQL | localhost:15432 | AWS RDS |
| Redis | localhost:6379 | AWS ElastiCache |
| RabbitMQ | localhost:5672 | AWS MQ |
| S3 | alaw-image-bucket | alaw-image-bucket (prod) |
| JWT 액세스 만료 | 1시간 | 7일 |
| CORS 허용 URL | localhost:3000 | a-law.site |
| 쿠키 Secure | false | true |
