<h1>try-finally보다는 try-with-resources를 사용하라</h1>

- 전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다.
- 자원이 둘 이상이면 try-finally 방식은 너무 지저분해진다.
- 자원이 둘 이상일 경우 try 블록과 finally 블록 모두에서 발생할 수 있는데 이는 두번째 예외가 첫번째 예외를 완전히 집어 삼키게 되며 첫번째 예외에 관한 정보는 남지 않게 되어 디버깅이 어렵게 된다.

<h2>try-with-resources</h2>

- 위와 같은 문제는 try-with-resources덕에 완전히 해결 되었다.
- 이 구조를 사용하려면 AutoCloseable 인터페이스를 구현해야 하며 단순히 void를 반환하는 close 메서드 하나만 정의한 인터페이스이다. 
    ```
    static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader {
            new FileReader(path)))
        }
    }
    ```
    ```
    // 복수 자원을 처리하는 예제
    static void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0)
                    out.write(buf, 0, n);
            }
    }
    ```
    - firstLineOfFile 메서드를 보면 readLine과 close 호출 양쪽에서 예외가 발생하면 close에서 발생한 예외는 숨겨지고 readLine에서 발생한 에외가 기록된다.
    - 숨겨진 예외들도 그냥 버려지지는 않고 스택 추적 내역에 숨겨졌다 꼬리표를 달고 출력된다.
    - catch절을 사용하여 try문을 더 중첩하지 않고 다수의 예외를 처리할 수 있다.