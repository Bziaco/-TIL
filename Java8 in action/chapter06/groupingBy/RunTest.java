package EnumGrouping;

import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RunTest {
	public static void main(String[] args) {
		List<BlogPost> posts = Arrays.asList(
				new BlogPost("A post","관리자",BlogPostType.NEWS,10),
				new BlogPost("B post","관리자",BlogPostType.NEWS,4),
				new BlogPost("C post","배재환",BlogPostType.REVIEW,21),
				new BlogPost("D post","배재환",BlogPostType.REVIEW,42),
				new BlogPost("E post","이채원",BlogPostType.REVIEW,132),
				new BlogPost("F post","이민형",BlogPostType.GUIDE,132),
				new BlogPost("G post","김태희",BlogPostType.GUIDE,39));
		
		Map<BlogPostType, List<BlogPost>> postType = posts.stream().collect(Collectors.groupingBy(BlogPost::getType));
		System.out.println(postType);
		// 결과 값 : {NEWS=[A post - 관리자, B post - 관리자], GUIDE=[F post - 이민형, G post - 김태희], REVIEW=[C post - 배재환, D post - 배재환, E post - 이채원]}
		
		
		Map<Tuple, List<BlogPost>> postType1 = posts.stream().collect(Collectors.groupingBy(post -> new Tuple(post.getType(),post.getAuth())));
		System.out.println(postType1);
		// 결과 값 : {REVIEW(배재환)=[C post - 배재환], GUIDE(김태희)=[G post - 김태희], GUIDE(이민형)=[F post - 이민형], NEWS(관리자)=[B post - 관리자], REVIEW(배재환)=[D post - 배재환], REVIEW(이채원)=[E post - 이채원], NEWS(관리자)=[A post - 관리자]} 
		
		Map<BlogPostType, Set<BlogPost>> returnValueSet = posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.toSet()));
		System.out.println(returnValueSet);
		// 결과 값 : {NEWS=[B post - 관리자, A post - 관리자], GUIDE=[G post - 김태희, F post - 이민형], REVIEW=[D post - 배재환, E post - 이채원, C post - 배재환]} 
		
		Map<String, Map<BlogPostType, List<BlogPost>>> MultiField = posts.stream().collect(Collectors.groupingBy(BlogPost::getAuth, Collectors.groupingBy(BlogPost::getType)));
		System.out.println(MultiField);
		// 결과 값 : {관리자={NEWS=[A post - 관리자, B post - 관리자]}, 이채원={REVIEW=[E post - 이채원]}, 배재환={REVIEW=[C post - 배재환, D post - 배재환]}, 김태희={GUIDE=[G post - 김태희]}, 이민형={GUIDE=[F post - 이민형]}}
		
		Map<BlogPostType, Integer> likePerType = posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.summingInt(BlogPost::getLikes)));
		System.out.println(likePerType);
		// 결과 값 : {NEWS=14, GUIDE=171, REVIEW=195}
		
		Map<BlogPostType, Optional<BlogPost>> maxLikesPerPostType = posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.maxBy(Comparator.comparingInt(BlogPost::getLikes))));
		System.out.println(maxLikesPerPostType);
		// 결과 값 : {NEWS=Optional[A post - 관리자], GUIDE=Optional[F post - 이민형], REVIEW=Optional[E post - 이채원]}
		
		Map<BlogPostType, IntSummaryStatistics> likeStatisticsPerType = posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.summarizingInt(BlogPost::getLikes)));
		System.out.println(likeStatisticsPerType);
		// 결과 값 : {NEWS=IntSummaryStatistics{count=2, sum=14, min=4, average=7.000000, max=10}, GUIDE=IntSummaryStatistics{count=2, sum=171, min=39, average=85.500000, max=132}, REVIEW=IntSummaryStatistics{count=3, sum=195, min=21, average=65.000000, max=132}}
		
		Map<BlogPostType, String> postsPerType = posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.mapping(BlogPost::getTitle, Collectors.joining(", ", "Post title: [", "]"))));
		System.out.println(postsPerType);
		// 결과 값 : {NEWS=Post title: [A post, B post], GUIDE=Post title: [F post, G post], REVIEW=Post title: [C post, D post, E post]}
	}
}

