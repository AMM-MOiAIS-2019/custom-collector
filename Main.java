import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Main {
	public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioningByUniqueness() {
		return Collector.<T, Map.Entry<List<T>, Set<T>>, Map<Boolean, List<T>>>of(
				() -> new AbstractMap.SimpleImmutableEntry<>(new ArrayList<T>(), new LinkedHashSet<>()), (c, e) -> {
					if (!c.getValue().add(e)) {
						c.getKey().add(e);
					}
				}, (c1, c2) -> {
					c1.getKey().addAll(c2.getKey());
					for (T e : c2.getValue()) {
						if (!c1.getValue().add(e)) {
							c1.getKey().add(e);
						}
					}
					return c1;
				}, c -> {
					Map<Boolean, List<T>> result = new HashMap<>(2);
					result.put(Boolean.FALSE, c.getKey());
					result.put(Boolean.TRUE, new ArrayList<>(c.getValue()));
					return result;
				});
	}

	public static void main(String[] args) {
		Map<Boolean, List<Integer>> map = Stream.of(1, 2, 3, 1, 9, 2, 5, 3, 4, 8, 2)
				.collect(partitioningByUniqueness());
		map.forEach((key, nums) -> {
			System.out.println();
			System.out.println(key.toString());
			nums.forEach(System.out::print);
		});
	}
}
