-- 마이그레이션 버전: V3
-- 설명: 영화(title)와 인물(name)에 대해 Ngram 기반 Full-Text 인덱스 추가
-- 작성일: 2024-12-06

-- ADD Full-Text + Ngram Index
ALTER TABLE movie
    ADD FULLTEXT INDEX title_ngram_idx (title) WITH PARSER ngram;

ALTER TABLE people
    ADD FULLTEXT INDEX name_ngram_idx (name) WITH PARSER ngram;