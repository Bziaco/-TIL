<h2>브랜치란 무엇인가</h2>
> 브랜치란 무엇인가

- Git이 브랜치를 다루는 과정을 이해하려면 우선 Git이 데이터를 어떻게 저장하는지 알아야 한다.

    ```
    $ git add README test.rb LICENSE
    $ git commit -m 'The initial commit of my project'
    ```
    [gitCommitStructure]
    ![gitCommitStructure](./img/gitCommitStructure.PNG)
- 다시 파일을 수정하고 커밋하면 이전 커밋이 무엇인지도 저장한다.

    [gitPushStructure]
    ![gitPushStructure](./img/gitPushStructure.PNG)

> 새 브랜치 생성하기
```
$ git branch testing
```
- Git은 'HEAD’라는 특수한 포인터가 있다. 이 포인터는 지금 작업하는 로컬 브랜치를 가리킨다.

    [workingForBranchToPointerHead]
    ![workingForBranchToPointerHead](./img/workingForBranchToPointerHead.PNG)

- git log 명령에 --decorate 옵션을 사용하면 쉽게 브랜치가 어떤 커밋을 가리키는지도 확인할 수 있다.
    ```
    $ git log --oneline --decorate
    f30ab (HEAD -> master, testing) add feature #32 - ability to add new formats to the central interface
    34ac2 Fixed bug #1328 - stack overflow under certain conditions
    98ca9 The initial commit of my project
    ```

> 브랜치 이동하기
- git checkout 명령으로 다른 브랜치로 이동할 수 있다. 
    ```
    $ git checkout testing
    ```

    [headFromTestingBranch]
    ![headFromTestingBranch](./img/headFromTestingBranch.PNG)
- 새로 만든 브랜치로 커밋을 해보자
    ```
    $ vim test.rb
    $ git commit -a -m 'made a change'
    ```
    [headFromTestingNewBranch]
    ![headFromTestingNewBranch](./img/headFromTestingNewBranch.PNG)
- 다시 master 브랜치로 돌아가보자
    ```
    $ git checkout master
    ```
    [headFromMasterBranch]
    ![headFromMasterBranch](./img/headFromMasterBranch.PNG)
- 이처럼 브랜치간의 이동을 통해 브랜치 마다 각각 다른 일을 독립적으로 수행할 수 있다.
- 파일을 수정하고 다시 커밋을 해보자.
    ```
    $ vim test.rb
    $ git commit -a -m 'made other changes'
    ```
    [splitBranch]
    ![splitBranch](./img/splitBranch.PNG)
- `git log --oneline --decorate --graph --all`
    ```
    $ git log --oneline --decorate --graph --all
    * c2b9e (HEAD, master) made other changes
    | * 87ab2 (testing) made a change
    |/
    * f30ab add feature #32 - ability to add new formats to the
    * 34ac2 fixed bug #1328 - stack overflow under certain conditions
    * 98ca9 initial commit of my project
    ```
    
    