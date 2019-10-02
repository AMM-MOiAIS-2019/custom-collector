package custom.collector;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class PartitioningByUniquenessCollector<T> implements Collector<T, Map.Entry<List<T>, Set<T>>, Map<Boolean, List<T>>> {

    @Override
    public Supplier<Entry<List<T>, Set<T>>> supplier() {
        return () -> new AbstractMap.SimpleImmutableEntry<>(new ArrayList<T>(), new LinkedHashSet<>());
    }

    @Override
    public BiConsumer<Entry<List<T>, Set<T>>, T> accumulator() {
        return (c, e) -> {
            if (!c.getValue().add(e)) {
                c.getKey().add(e);
            }
        };
    }

    @Override
    public BinaryOperator<Entry<List<T>, Set<T>>> combiner() {
        return (c1, c2) -> {
            c1.getKey().addAll(c2.getKey());
            for (T e : c2.getValue()) {
                if (!c1.getValue().add(e)) {
                    c1.getKey().add(e);
                }
            }
            return c1;
        };
    }

    @Override
    public Function<Entry<List<T>, Set<T>>, Map<Boolean,List<T>>> finisher() {
        return c -> {
            Map<Boolean, List<T>> result = new HashMap<>(2);
            result.put(Boolean.FALSE, c.getKey());
            result.put(Boolean.TRUE, new ArrayList<>(c.getValue()));
            return result;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }

}
