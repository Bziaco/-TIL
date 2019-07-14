<h2>1. Git vs SVN</h2>

> Git은 시간 순으로 스냅샷을 저장, SVN은 파일에 대한 변화를 저장

[SVN]
![SVNStream](./img/SVNStream.PNG)

[Git]
![GitStream](./img/GitStream.PNG)


> Git의 무결성 
 - Git은 SHA-1 해시를 사용하여 체크섬을 만든다.  
   (ex.24b9da6552252987aa493b52f8696cd6d3b00373)

 - 실제로 Git은 파일을 이름으로 저장하지 않고 해당 파일의 해시로 저장

> Git의 세가지 상태 
 - Committed란 데이터가 로컬 데이터베이스에 안전하게 저장됐다는 것을 의미
 - Modified는 수정한 파일을 아직 로컬 데이터베이스에 커밋하지 않은 것을 말
 - Staged란 현재 수정한 파일을 곧 커밋할 것이라고 표시한 상태를 의미
    
    ![workingTree](./img/workingTree.PNG)


<h2>2.Git 저장소 만들기</h2>
