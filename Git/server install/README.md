<h1>Git Server Install & Set-up</h1>

<h2>설치 환경</h2>

- Git Server : CentOS 7
- Client     : Windows 10


<h2>Git Install</h2>

```
# yum install git
```

<h2>Git Server Set-up</h2>

> 계정 생성(Git Server)
```
# adduser bjh2501   // bjh2501 계정 생성
# passwd bjh2501    // bjh2501 암호 설정
# su bjh2501        // bjh2501 계정으로 전환
# cd /home/bjh2501  // 홈 디렉토리로 이동

# ssh-keygen -t rsa  //rsa 암호화 방식으로 key 생성
Enter file in which to save the key (/home/hsck1/.ssh/id_rsa)   // enter
Enter passphrase (empty for no passphrase):                     // enter
Enter same passphrase again:                                    // enter

# ll ~/.ssh     // /home/bjh2501/.ssh에 id_rsa, id_rsa.pub 생성 여부 확인
# exit          // bjh2501 계정 로그아웃
```

> Git 설치 및 기본 설정(Client)
- Git 설치 <https://git-scm.com/download/win>
- Git 설치 완료 후 Git bash 실행

```
# git --version     // Git 버전 확인
# git config --global user.name "사용자 이름"       // 사용자 이름 등록
# git config --global user.email "사용자 이메일"    // 사용자 이메일 등록
# git config --global color.ui "auto"              // 색상 자동 설정
# git config --global --list                       // 설정 확인
```

> SSH Key 생성(Client)
```
# ssh-keygen -t rsa     //rsa 암호화 방식으로 key 생성
Generating public/private rsa key pair.
Enter file in which to save the key (/c/Users/hsck/.ssh/id_rsa):    // enter
Enter passphrase (empty for no passphrase):                         // enter
Enter same passphrase again:                                        // enter
# ll ~/.ssh     // 해당 경로에 id_rsa, id_rsa.pub 파일 생성 여부 확인
```

> Public Key를 Git server에 전송(Client)
```
# cd ~/.ssh     // 해당 경로로 이동(id_rsa가 생성된 폴더)
# scp id_rsa.pub bjh2501@서버도메인:/home/bjh2501 
// Git Server 계정의 home 디렉토리에 공개피 복사
// 권한에러가 난다면 vi /etc/ssh/sshd_config 접속
// PasswordAuthentication를 yes로 변경
```

> 전송받은 Public Key 등록(Git Server)
```
# ssh bjh2501@서버도메인    // ssh로 서버 원격 접속
# cd /home/bjh2501  // 홈 디렉토리 이동
# ll    // 전송받은 id_rsa.pub 파일 존재 여부 확인
# cat id_rsa.pub >> .ssh/authorized_keys    // authorized_keys 파일 생성 후 id_rsa.pub 내용 추가
# rm -f id_rsa.pub // id_rsa.pub 삭제
# chmod 700 .ssh    //.ssh 디렉토리 권한 설정
# chmod 600 .ssh/authorized_keys    // authorized_keys 파일 권한 설정
# exit 
```

> 접속 테스트(Client)
```
# ssh git@서버도메인    // 암호를 물어보지 않으면 성공
# exit
```

> Git Repository 생성(Client)
```
# ssh root@서버도메인 
// root 계정으로 ssh 원격 접속
// 만약 root로 접속이 안된다면 vi /etc/ssh/sshd_config 로 접속
// PermitRootLogin 을 yes로 변경 
// 추가적인 ssh 보안 관련 기능은 ssh security 참고
# mkdir -p /data/source/git/project.git     // 아래 디렉토리 생성
# chown bjh2501:bjh2501 /data/source/git    // 소유권 변경
# chown -R bjh2501 /data/source/git/project.git     // 소유권 변경
# su bjh2501    // 해당 계정으로 전환

# cd /data/source/git/project.git   // 해당 디렉토리 이동
# git --bare init      // 디렉토리 초기화
```

> clone & commit(Client)
```
# cd c:\bjh2501   // 임의 디렉토리로 이동
# mkdir project     // project 폴더 생성
# git clone bjh2501@서버도메인:/data/source/git/project.git
```

> .gitignore 설정(Client)
- cd c:\bjh2501/project/
- touch .gitignore 파일 생성
- vi .gitignore
/*
  .classpath
  .settings/
  target/
  .class
  처럼 커밋에 불필요한 파일의 경로를 설정
*/
- 만약 ignore가 반영 되지 않을 경우
```
# git rm -r --cached .
# git add .     // add 시 CRLF will be replaced by LF 와 같은 에러가 생기면 Git Server에서
                  윈도우 사용자: `git config --global core.autocrlf true`
                  맥/리눅스 사용자 : `git config --global core.autocrlf true input`
                  혹은 그냥 에러메세지만 끄고 싶은 경우 : git config --global core.safecrlf false
# git commit -m "git ignore add"
# git push
```






