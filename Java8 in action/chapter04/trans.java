package java8.java8;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import java.util.stream.Collectors;

public class tran {
	public static void main(String[] args) {
		Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
		
		List<Transaction> transactions = Arrays.asList(
            new Transaction(brian, 2011, 300), 
            new Transaction(raoul, 2012, 1000),
            new Transaction(raoul, 2011, 400),
            new Transaction(mario, 2012, 710),	
            new Transaction(mario, 2012, 700),
            new Transaction(alan, 2012, 950)
        );	
		
		List<Transaction> tr2011 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011)
                .sorted(comparing(Transaction::getValue))
                .collect(toList());
		
		System.out.println(tr2011);
		
		List<String> traderCity = transactions.stream()
				.map(transaction -> transaction.getTrader().getCity())
				.distinct()
				.collect(toList());
		System.out.println(traderCity);
		
		List<Trader> trCambridgeName = transactions.stream()
				.map(Transaction::getTrader)
				.filter(trader -> trader.getCity().equals("Cambridge"))
				.distinct()
				.sorted(comparing(Trader::getName))
				.collect(toList());
		
		System.out.println(trCambridgeName);
		
		String alphaName = transactions.stream()
				.map(transaction -> transaction.getTrader().getName())
				.distinct()
				.sorted()
				//.reduce("", (a,b) -> a+b)
				.collect(Collectors.joining());
				
		System.out.println(alphaName);
		
		boolean isTraderFromMilan = transactions.stream()
				/*.map(Transaction::getTrader)
				.anyMatch(w -> w.getName().equals("Milan"));*/
				.anyMatch(Transaction -> Transaction.getTrader().getName().equals("Milan"));
		
		System.out.println(isTraderFromMilan);
		
		transactions.stream()
				.filter(Transaction -> Transaction.getTrader().getCity().equals("Cambridge"))
				.map(Transaction::getValue)
				.forEach(System.out::println);
				//.collect(toList());
		
		Optional<Integer> maxValue = transactions.stream()
					.map(Transaction::getValue)
					.reduce(Integer::max);
		
		System.out.println(maxValue);
				
		
		Optional<Integer> minValue = transactions.stream()
				.map(Transaction::getValue)
				.reduce(Integer::min);
	
		System.out.println(minValue);
			
	}
}
