# 2024 캡스톤디자인 - 의지박약 어플 개발 [백엔드]

# 1. 팀원👨🏻‍💻
이제용 / 이영웅 / 옥정빈 / 김우성
---
# 2. 규칙

### 커밋 규칙 

- **feat**: 새로운 기능 추가  
- **fix**: 버그 수정  
- **docs**: 문서 수정  
- **style**: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우  
- **refactor**: 코드 리팩토링  
- **test**: 테스트 코드, 리팩토링 테스트 코드 추가  
- **chore**: 빌드 업무 수정, 패키지 매니저 수정  

- 예시 : {feat} 로그인 기능 추가

### branch 규칙

각자의 영어 이름을 딴 branch 명을 생성하여 사용.  
  
예시:
- git checkout -b <브랜치명>      
- git checkout -b jeyong

### merge 규칙
  
rebase and merge  
```
# main 브랜치에서  
git fetch origin main
git pull
git checkout {내 브랜치}
git rebase main
# -> conflict 해결
git push origin {내 브랜치}
# -> PR 작성
```
