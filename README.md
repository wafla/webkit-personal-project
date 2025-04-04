# WebKit Personal Project

## 📌 프로젝트 개요

### 🎯 주제
외국인이 친구를 만들 수 있는 커뮤니티 웹사이트 개발 (백엔드 중심)

### ⏳ 개발 기간
11일

### 🛠️ 개발 기술
- **개발 툴**: VSCode, IntelliJ
- **프론트엔드**: React
- **백엔드**: Spring, Spring Security
- **인증 방식**: JWT 토큰을 활용한 사용자 인증

## 🔑 주요 기능

### 🏠 회원가입 및 로그인
- JWT 토큰을 활용한 사용자 인증 및 로그인 유지
- 리프레시 토큰을 이용한 자동 로그인 연장

### 📝 프로필
- 프로필 사진, 학교, 생일, 국적, 성별, 취미 정보 관리

### 📰 게시글
- 게시글 작성 및 페이징 처리
- 사진 첨부 기능
- 댓글 작성 및 조회수 증가 기능

### 💬 메시지
- 사용자 간 1:1 메시지 전송 및 확인 기능

### 🖼️ 사진 파일
- 게시글 및 프로필에 사진 첨부 가능
- 글 수정 및 프로필 수정 시, 기존 사진 유지 가능

## 🔮 향후 추가 예정 기능
- 게시글 / 유저 검색 기능
- 이메일 인증 기능
- 번역 AI API를 활용한 다국어 지원

## ⚠️ 현재 문제점 및 개선 방향
- 백엔드에서 유저 정보를 충분히 검사하지 않아 보안 취약점 존재 → 인증 및 권한 검증 강화 필요
- 일정 시간이 지나면 로그아웃 시 문제가 발생 → 토큰 갱신 로직 개선 필요

## ✨ 마무리
- 정해진 기간안에 주요 기능들을 완성하기 위해 회원가입, 로그인/로그아웃, 글 작성, 페이징 처리 기능이 구현된 프로그램을 활용하여 목표한 기능들을 구현함
- 학교 재학생들과 유학생들이 소통할 수 있는 공간을 목표로 했지만 시간이 부족해 번역 기능을 넣지 못한것이 아쉬움

## 📸 구현 화면
### 메인화면(글작성)
![image](https://github.com/user-attachments/assets/c17307e9-315f-4355-bd62-9a0bd49b7f60)
### 글 확인
![image](https://github.com/user-attachments/assets/74233af2-90bb-4571-a173-264ed5278782)
### 글 작성
![image](https://github.com/user-attachments/assets/c4dcc5cb-7fa7-4245-908b-03ff043adaa7)
### 회원가입
![image](https://github.com/user-attachments/assets/3e7f4a57-9e2e-4a5f-9b30-888d85bfde11)
### 프로필정보(본인 계정이 아닐 경우)
![image](https://github.com/user-attachments/assets/e3eebb80-86ee-4588-bff6-d2b9c84003ba)
### 프로필정보(본인 계정인 경우)
![image](https://github.com/user-attachments/assets/8ec00f35-75ec-4915-8249-4a0e73286442)
### 메시지(대화한 유저들 확인)
![image](https://github.com/user-attachments/assets/ead192eb-c1b6-495f-a610-22d0ec897162)
### 메시지 확인, 전송
![image](https://github.com/user-attachments/assets/2e6ca48e-a7f3-4cad-8e7d-56838989797d)
### 로그인
![image](https://github.com/user-attachments/assets/b3e04b91-0897-4c00-bfe0-ec21b0628f15)
