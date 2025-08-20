-- 테스트용 기본 데이터 삽입 (MySQL과 H2 호환)

-- Skills 테이블 초기 데이터
INSERT IGNORE INTO skills (name) VALUES ('Java');
INSERT IGNORE INTO skills (name) VALUES ('Spring Boot');
INSERT IGNORE INTO skills (name) VALUES ('JavaScript');
INSERT IGNORE INTO skills (name) VALUES ('React');
INSERT IGNORE INTO skills (name) VALUES ('Python');
INSERT IGNORE INTO skills (name) VALUES ('제품 전략');
INSERT IGNORE INTO skills (name) VALUES ('사용자 연구');
INSERT IGNORE INTO skills (name) VALUES ('애자일 개발');
INSERT IGNORE INTO skills (name) VALUES ('모바일');

-- Companies 테이블 초기 데이터
INSERT IGNORE INTO companies (name) VALUES ('네이버');
INSERT IGNORE INTO companies (name) VALUES ('카카오');
INSERT IGNORE INTO companies (name) VALUES ('삼성전자');
INSERT IGNORE INTO companies (name) VALUES ('LG전자');
INSERT IGNORE INTO companies (name) VALUES ('스타트업');

-- Positions 테이블 초기 데이터
INSERT IGNORE INTO positions (name, category, description) VALUES ('소프트웨어 엔지니어', 'IT', '소프트웨어 개발 및 설계를 담당하는 포지션입니다.');
INSERT IGNORE INTO positions (name, category, description) VALUES ('프로덕트 매니저', 'PM', '제품 기획 및 관리를 담당하는 포지션입니다.');
INSERT IGNORE INTO positions (name, category, description) VALUES ('데이터 사이언티스트', 'Data', '데이터 분석 및 모델링을 담당하는 포지션입니다.');
INSERT IGNORE INTO positions (name, category, description) VALUES ('UX/UI 디자이너', 'Design', '사용자 경험 및 인터페이스 설계를 담당하는 포지션입니다.');
INSERT IGNORE INTO positions (name, category, description) VALUES ('백엔드 개발자', 'IT', '서버 및 백엔드 시스템 개발을 담당하는 포지션입니다.');
INSERT IGNORE INTO positions (name, category, description) VALUES ('프론트엔드 개발자', 'IT', '프론트엔드 개발 및 UI 구현을 담당하는 포지션입니다.');

-- Provinces 테이블 초기 데이터
INSERT IGNORE INTO provinces (`key`, name) VALUES ('seoul', '서울특별시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('busan', '부산광역시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('daegu', '대구광역시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('incheon', '인천광역시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('gwangju', '광주광역시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('daejeon', '대전광역시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('ulsan', '울산광역시');
INSERT IGNORE INTO provinces (`key`, name) VALUES ('gyeonggi', '경기도');

-- Cities 테이블 초기 데이터
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('gangnam', '강남구', 1);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('seocho', '서초구', 1);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('songpa', '송파구', 1);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('mapo', '마포구', 1);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('yongsan', '용산구', 1);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('haeundae', '해운대구', 2);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('busanjin', '부산진구', 2);
INSERT IGNORE INTO cities (`key`, name, province_id) VALUES ('suyeong', '수영구', 2);

-- Users 테이블 초기 데이터 (멘토) - email은 unique이므로 INSERT IGNORE 사용
INSERT IGNORE INTO users (name, email, role, provider, provider_id, profile_image_url, province_id, city_id) VALUES
('김민수', 'mentor1@example.com', 'MENTOR', 'kakao', '3024567890', 'https://example.com/profile1.jpg', 1, 1),
('이지은', 'mentor2@example.com', 'MENTOR', 'kakao', '3024567891', 'https://example.com/profile2.jpg', 1, 2),
('박준호', 'mentor3@example.com', 'MENTOR', 'kakao', '3024567892', 'https://example.com/profile3.jpg', 1, 3),
('최수진', 'mentor4@example.com', 'MENTOR', 'kakao', '3024567893', 'https://example.com/profile4.jpg', 1, 4),
('정태영', 'mentor5@example.com', 'MENTOR', 'kakao', '3024567894', 'https://example.com/profile5.jpg', 1, 5),
('한소영', 'mentor6@example.com', 'MENTOR', 'kakao', '3024567895', 'https://example.com/profile6.jpg', 2, 6),
('조현우', 'mentor7@example.com', 'MENTOR', 'kakao', '3024567896', 'https://example.com/profile7.jpg', 2, 7),
('윤미래', 'mentor8@example.com', 'MENTOR', 'kakao', '3024567897', 'https://example.com/profile8.jpg', 2, 8);

-- Users 테이블 초기 데이터 (멘티)
INSERT IGNORE INTO users (name, email, role, provider, provider_id, profile_image_url, province_id, city_id) VALUES
('김학생', 'mentee1@example.com', 'MENTEE', 'kakao', '3024567898', 'https://example.com/mentee1.jpg', 1, 1),
('이새내기', 'mentee2@example.com', 'MENTEE', 'kakao', '3024567899', 'https://example.com/mentee2.jpg', 1, 2),
('박취준생', 'mentee3@example.com', 'MENTEE', 'kakao', '3024567900', 'https://example.com/mentee3.jpg', 1, 3);

-- PersonalityTags 테이블 초기 데이터
INSERT IGNORE INTO personality_tags (name) VALUES ('적극적');
INSERT IGNORE INTO personality_tags (name) VALUES ('신중함');
INSERT IGNORE INTO personality_tags (name) VALUES ('창의적');
INSERT IGNORE INTO personality_tags (name) VALUES ('분석적');
INSERT IGNORE INTO personality_tags (name) VALUES ('협력적');
INSERT IGNORE INTO personality_tags (name) VALUES ('독립적');
INSERT IGNORE INTO personality_tags (name) VALUES ('도전적');
INSERT IGNORE INTO personality_tags (name) VALUES ('안정적');

-- Mentors 테이블 초기 데이터 (email로 user를 찾아서 처리)
INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 1, 1, '5년차 백엔드 개발자입니다. Spring Boot와 AWS 전문가입니다.', '안녕하세요! 현재 네이버에서 백엔드 개발자로 일하고 있는 김민수입니다. Java와 Spring Boot를 주력으로 하며, 특히 대용량 트래픽 처리와 시스템 아키텍처 설계에 경험이 많습니다. 개발자로 성장하고 싶은 분들께 실질적인 조언을 드리겠습니다.', 5, true
FROM users u WHERE u.email = 'mentor1@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 2, 2, '7년차 프로덕트 매니저입니다. 사용자 중심 서비스 기획 전문가입니다.', '카카오에서 프로덕트 매니저로 일하고 있는 이지은입니다. 다양한 서비스의 기획부터 런칭까지 전 과정을 경험했습니다. PM으로의 커리어 전환이나 서비스 기획에 관심 있는 분들과 이야기 나누고 싶습니다.', 7, true
FROM users u WHERE u.email = 'mentor2@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 3, 3, '6년차 데이터 사이언티스트입니다. 머신러닝과 데이터 분석 전문가입니다.', '삼성전자에서 데이터 사이언티스트로 근무 중인 박준호입니다. Python, SQL, 머신러닝 모델링 등에 전문성을 가지고 있습니다. 데이터 분야로 진로를 고민하시는 분들께 도움을 드리겠습니다.', 6, true
FROM users u WHERE u.email = 'mentor3@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 4, 3, '4년차 UX/UI 디자이너입니다. 사용자 경험 설계 전문가입니다.', '삼성전자에서 UX/UI 디자이너로 일하고 있는 최수진입니다. 모바일 앱과 웹 서비스의 사용자 경험을 설계하고 있습니다. 디자인 분야로의 커리어 전환이나 UX 역량 향상에 대해 상담해 드립니다.', 4, true
FROM users u WHERE u.email = 'mentor4@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 5, 4, '8년차 프론트엔드 개발자입니다. React와 TypeScript 전문가입니다.', 'LG전자에서 프론트엔드 개발자로 근무 중인 정태영입니다. React, Vue.js, TypeScript 등 모던 프론트엔드 기술에 깊은 이해를 가지고 있습니다. 프론트엔드 개발자로 성장하고 싶은 분들을 응원합니다.', 8, true
FROM users u WHERE u.email = 'mentor5@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 6, 1, '3년차 백엔드 개발자입니다. Node.js와 Python 전문가입니다.', '네이버에서 백엔드 개발자로 일하고 있는 한소영입니다. Node.js와 Python을 주로 사용하며, API 설계와 데이터베이스 최적화에 관심이 많습니다. 주니어 개발자의 성장을 도와드리겠습니다.', 3, true
FROM users u WHERE u.email = 'mentor6@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 1, 2, '10년차 시니어 개발자입니다. 풀스택 개발과 팀 리딩 전문가입니다.', '카카오에서 시니어 개발자로 근무하는 조현우입니다. 풀스택 개발 경험과 팀 리딩 경험을 바탕으로 개발자의 전반적인 커리어 성장에 대해 조언해 드립니다. 기술적 성장뿐만 아니라 리더십 개발에도 관심이 있습니다.', 10, true
FROM users u WHERE u.email = 'mentor7@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

INSERT INTO mentors (user_id, position_id, company_id, description, introduction, experience, is_verified) 
SELECT u.id, 2, 5, '5년차 스타트업 PM입니다. 0-1 프로덕트 개발 전문가입니다.', '스타트업에서 프로덕트 매니저로 일하고 있는 윤미래입니다. 초기 제품 개발부터 시장 확장까지 다양한 단계의 프로덕트를 경험했습니다. 스타트업 환경에서의 PM 역할과 빠른 성장에 대해 이야기 나누고 싶습니다.', 5, true
FROM users u WHERE u.email = 'mentor8@example.com' AND NOT EXISTS (SELECT 1 FROM mentors m WHERE m.user_id = u.id);

-- MentorSkills 테이블 초기 데이터 (멘토-스킬 매핑)
INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor1@example.com' AND m.user_id = u.id AND s.name = 'Java' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor1@example.com' AND m.user_id = u.id AND s.name = 'Spring Boot' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor2@example.com' AND m.user_id = u.id AND s.name = '제품 전략' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor2@example.com' AND m.user_id = u.id AND s.name = '사용자 연구' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor3@example.com' AND m.user_id = u.id AND s.name = 'Python' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor3@example.com' AND m.user_id = u.id AND s.name = '제품 전략' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor4@example.com' AND m.user_id = u.id AND s.name = '사용자 연구' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor4@example.com' AND m.user_id = u.id AND s.name = '모바일' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor5@example.com' AND m.user_id = u.id AND s.name = 'JavaScript' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor5@example.com' AND m.user_id = u.id AND s.name = 'React' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor6@example.com' AND m.user_id = u.id AND s.name = 'Python' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor6@example.com' AND m.user_id = u.id AND s.name = 'Spring Boot' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor7@example.com' AND m.user_id = u.id AND s.name = 'Java' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor7@example.com' AND m.user_id = u.id AND s.name = 'JavaScript' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor7@example.com' AND m.user_id = u.id AND s.name = 'React' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor8@example.com' AND m.user_id = u.id AND s.name = '제품 전략' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

INSERT INTO mentor_skills (mentor_id, skill_id) 
SELECT m.id, s.id FROM mentors m, skills s, users u 
WHERE u.email = 'mentor8@example.com' AND m.user_id = u.id AND s.name = '애자일 개발' 
AND NOT EXISTS (SELECT 1 FROM mentor_skills ms WHERE ms.mentor_id = m.id AND ms.skill_id = s.id);

-- UserPersonalityTags 테이블 초기 데이터 (사용자-성향태그 매핑)
INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentor1@example.com' AND pt.name = '적극적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentor1@example.com' AND pt.name = '분석적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentor2@example.com' AND pt.name = '신중함' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentor2@example.com' AND pt.name = '협력적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentor3@example.com' AND pt.name = '창의적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentor3@example.com' AND pt.name = '분석적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentee1@example.com' AND pt.name = '적극적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentee1@example.com' AND pt.name = '창의적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentee2@example.com' AND pt.name = '신중함' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentee2@example.com' AND pt.name = '분석적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentee3@example.com' AND pt.name = '협력적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);

INSERT INTO user_personality_tags (user_id, personality_tag_id) 
SELECT u.id, pt.id FROM users u, personality_tags pt 
WHERE u.email = 'mentee3@example.com' AND pt.name = '도전적' 
AND NOT EXISTS (SELECT 1 FROM user_personality_tags upt WHERE upt.user_id = u.id AND upt.personality_tag_id = pt.id);