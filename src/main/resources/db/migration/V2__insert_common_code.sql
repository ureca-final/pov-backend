-- 공통코드 그룹 데이터 삽입
INSERT INTO common_code_group (common_code_group_description, common_code_group_name, is_active, group_code)
VALUES
    ('영화 장르', '장르', 1, '010'),
    ('리뷰 키워드', '키워드', 1, '020');

-- 공통코드 데이터 삽입 - 장르
INSERT INTO common_code (code, common_code_description, common_code_name, group_code, is_active)
VALUES
    ('01', 'Action', '액션', '010', 1),
    ('02', 'Adventure', '모험', '010', 1),
    ('03', 'Animation', '애니메이션', '010', 1),
    ('04', 'Comedy', '코미디', '010', 1),
    ('05', 'Crime', '범죄', '010', 1),
    ('06', 'Disaster Film', '재난', '010', 1),
    ('07', 'Documentary', '다큐멘터리', '010', 1),
    ('08', 'Drama', '드라마', '010', 1),
    ('09', 'Eastern', '동양', '010', 1),
    ('10', 'Erotic Film', '에로', '010', 1),
    ('11', 'Exploitation Film', '익스플로이테이션', '010', 1),
    ('12', 'Fantasy', '판타지', '010', 1),
    ('13', 'Found Footage', '파운드 푸티지', '010', 1),
    ('14', 'Historical Film', '역사', '010', 1),
    ('15', 'Horror', '공포', '010', 1),
    ('16', 'Musicals', '뮤지컬', '010', 1),
    ('17', 'Road Movie', '로드 무비', '010', 1),
    ('18', 'Science Fiction', 'SF', '010', 1),
    ('19', 'Sport Film', '스포츠', '010', 1),
    ('20', 'Thriller', '스릴러', '010', 1),
    ('21', 'TV Show', 'TV프로그램', '010', 1),
    ('22', 'Western', '서부', '010', 1);

-- 공통코드 데이터 삽입 - 키워드
INSERT INTO common_code (code, common_code_description, common_code_name, group_code, is_active)
VALUES
    ('01', '긍정', '감동적인', '020', 1),
    ('02', '긍정', '재미있는', '020', 1),
    ('03', '긍정', '몰입감 있는', '020', 1),
    ('04', '긍정', '연기력이 뛰어난', '020', 1),
    ('05', '긍정', '연출이 뛰어난', '020', 1),
    ('06', '부정', '지루한', '020', 1),
    ('07', '부정', '연기가 어색한', '020', 1),
    ('08', '부정', '연출이 어색한', '020', 1),
    ('09', '부정', '전개가 느린', '020', 1),
    ('10', '부정', '기대 이하의', '020', 1);
