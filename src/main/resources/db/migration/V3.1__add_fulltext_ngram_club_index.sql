-- 마이그레이션 버전: V3.1
-- 설명: 클럽 이름(name)와 클럽 리더(nickname)에 대해 Ngram 기반 Full-Text 인덱스 추가
-- 작성일: 2024-12-09

-- ADD Full-Text + Ngram Index
ALTER TABLE club
    ADD FULLTEXT INDEX clubname_ngram_idx (name) WITH PARSER ngram;

ALTER TABLE member
    ADD FULLTEXT INDEX nickname_ngram_idx (nickname) WITH PARSER ngram;
