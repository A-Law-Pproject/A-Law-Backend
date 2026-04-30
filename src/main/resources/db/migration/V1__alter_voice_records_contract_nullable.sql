-- voice-only 모드 지원을 위해 voice_records.contract_id NOT NULL 제약을 제거한다.
-- 기존 데이터는 영향을 받지 않는다 (기존 행의 contract_id 값은 유지됨).
ALTER TABLE voice_records ALTER COLUMN contract_id DROP NOT NULL;
