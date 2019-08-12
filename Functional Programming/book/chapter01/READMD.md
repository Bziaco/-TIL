<h2>왜</h2>

> 패러다임의 전환
- 언어란 기반이 되는 기술이 뒷받침 되어야 비로소 보편화가 이뤄질 수 있다.
- 먼저 텍스트 파일을 읽고 가장 많이 사용된 단어들을 찾고 그 단어들과 빈도를 정렬된 목록으로 출력하는 예제를 보자.
    ```
    public class words {
        private Set<String> NON_WORDS = new HashMap<String>(){{
            add("the"); add("and");	add("of"); add("to"); add("a");
            add("i"); add("it"); add("in");	add("or"); add("is");
            add("d"); add("s"); add("as"); add("so"); add("but");
            add("be");
        }};

        public Map wordFreq(String words) {
            //키값을 저장 할 맵
            TreeMap<String, Integer> wordMap = new TreeMap<String, Integer>();
            Matcher m = Pattren.compile("\\w+").matcher(words);
            while(m.find()) {
                String word = m.group().toLowerCase();
                if(!NON_WORDS.contain(word)) {
                    if(wordMap.get(word) == null) {
                        wordMap.put(word,1);
                    } else {
                        wordMap.put(word, wordMap.get(word) +1);
                    }
                }
            }
            return wordMap;
        }
    }
    ```

- 위 예제를 람다를 이용한 교계함수를 사용해서 코드를 리펙토링 해보자
    ```
    private List<String> regexToList(String words, String regex) {
        List wordList = new ArrayList<>();
        Matcher m = pattren.complie(regex).matcher(words);
        while(m.find()) {
            wordList.add(m.group());
        }

        return wordList;
    }

    public Map wordFreq(String words) {
        TreeMap<String, Integer> wordMap = new TreeMap<>();
        regexToList(words, "\\w+").stream()
                                  .map(w -> w.toLowerCase())
                                  .filter(w -> !NON_WORDS.contain(w))
                                  .foreach(w -> wordMap.put(w, wordMap.getOrDefault(w,0) + 1));
        return wordMap;
    }
    ```
    - 리펙토링 하기 전 코드는 세 연산을 한번의 반복자 블록에서 실행함으로서 성능을 명료함과 맞바꾸었다. 이는 보편화된 트레이드오프지만 필자는 권장하지 않는다.
    - 명령형 프로그래밍을 하다 보면 효율을 높이기 위해 여러작업을 한 루프로 복잡하게 하는 경우가 종종 있다. 함수형 프로그램이에서는 map()이나 filter() 같은 고계함수를 통해 추상화 단계를 높여서 문제를 더욱 명료하게 볼수 있다.


>언어 트렌트에 발맞추기
- 함수의 연산결과를 계속 저장해 두었다가 같은 입력이 주어지면 연산을 하지 않고 리턴하는 최적화 기법이 메모이제이션이다.

> 간결함
- 함수형 언어는 가변 상태를 제어하는 메커니즘을 구축하기 보다 그런 움직이는 부분을 아에 제거하는데 주력한다. 언어가 오류를 발생하기 쉬운 기능을 적게 노출하면 개발자가 오류를 만들 가능성도 줄어든다는 이론에 따른 것이다.
- 함수 수준의 캡슐화는 모든 문제에 대한 새로운 클래스 구조를 구축하는 것보다 세분화되고 기초적인 수준에서 재사용을 가능하게 한다. 
- 자바에는 자체적으로 내부 자료구조를 가진 수십개의 XML라이브러리가 있다. 이를 통해 개발자가 개입하지 않고도 모든 맵 작업이 성능 향상을 얻었다. 함수형 개발자는 적은 수의 자료구조와 그것들을 잘 이해하기 위한 최적화된 방법을 만들기를 선호한다.