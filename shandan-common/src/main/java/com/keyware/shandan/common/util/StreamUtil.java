package com.keyware.shandan.common.util;

import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Stream工具类
 *
 * @author GuoXin
 */
public class StreamUtil {

    /**
     * 将指定数据集合的Stream对象进行包装
     *
     * @param collection 数据集合
     * @param <T>        泛型类型
     * @return Stream包装器
     */
    public static <T> StreamWrapper<T> as(@NonNull Collection<T> collection) {
        return wrapper(collection.stream());
    }

    /**
     * 将指定的Stream对象进行包装
     *
     * @param stream Stream对象
     * @param <T>    泛型类型
     * @return Stream包装器
     */
    public static <T> StreamWrapper<T> wrapper(@NonNull Stream<T> stream) {
        return new StreamWrapper<>(stream);
    }

    /**
     * Stream包装器
     *
     * @author GuoXin
     */
    public static class StreamWrapper<T> implements Stream<T> {
        private Stream<T> stream;

        public StreamWrapper(Stream<T> stream) {
            this.stream = stream;
        }

        public List<T> toList() {
            return stream.collect(Collectors.toList());
        }

        public Set<T> toSet() {
            return stream.collect(Collectors.toSet());
        }


        @Override
        public StreamWrapper<T> filter(Predicate<? super T> predicate) {
            this.stream = stream.filter(predicate);
            return this;
        }

        @Override
        public <R> StreamWrapper<R> map(Function<? super T, ? extends R> mapper) {
            return new StreamWrapper<>(stream.map(mapper));
        }

        @Override
        public IntStream mapToInt(ToIntFunction<? super T> mapper) {
            return stream.mapToInt(mapper);
        }

        @Override
        public LongStream mapToLong(ToLongFunction<? super T> mapper) {
            return stream.mapToLong(mapper);
        }

        @Override
        public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
            return stream.mapToDouble(mapper);
        }

        @Override
        public <R> StreamWrapper<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
            return new StreamWrapper<>(stream.flatMap(mapper));
        }

        @Override
        public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
            return stream.flatMapToInt(mapper);
        }

        @Override
        public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
            return stream.flatMapToLong(mapper);
        }

        @Override
        public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
            return stream.flatMapToDouble(mapper);
        }

        @Override
        public StreamWrapper<T> distinct() {
            this.stream = stream.distinct();
            return this;
        }

        @Override
        public StreamWrapper<T> sorted() {
            this.stream = stream.sorted();
            return this;
        }

        @Override
        public StreamWrapper<T> sorted(Comparator<? super T> comparator) {
            this.stream = stream.sorted(comparator);
            return this;
        }

        @Override
        public StreamWrapper<T> peek(Consumer<? super T> action) {
            this.stream = stream.peek(action);
            return this;
        }

        @Override
        public StreamWrapper<T> limit(long maxSize) {
            this.stream = stream.limit(maxSize);
            return this;
        }

        @Override
        public StreamWrapper<T> skip(long n) {
            this.stream = stream.skip(n);
            return this;
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            stream.forEach(action);
        }

        @Override
        public void forEachOrdered(Consumer<? super T> action) {
            stream.forEachOrdered(action);
        }

        @Override
        public Object[] toArray() {
            return stream.toArray();
        }

        @Override
        public <A> A[] toArray(IntFunction<A[]> generator) {
            return stream.toArray(generator);
        }

        @Override
        public T reduce(T identity, BinaryOperator<T> accumulator) {
            return stream.reduce(identity, accumulator);
        }

        @Override
        public Optional<T> reduce(BinaryOperator<T> accumulator) {
            return stream.reduce(accumulator);
        }

        @Override
        public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
            return stream.reduce(identity, accumulator, combiner);
        }

        @Override
        public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
            return stream.collect(supplier, accumulator, combiner);
        }

        @Override
        public <R, A> R collect(Collector<? super T, A, R> collector) {
            return stream.collect(collector);
        }

        @Override
        public Optional<T> min(Comparator<? super T> comparator) {
            return stream.min(comparator);
        }

        @Override
        public Optional<T> max(Comparator<? super T> comparator) {
            return stream.max(comparator);
        }

        @Override
        public long count() {
            return stream.count();
        }

        @Override
        public boolean anyMatch(Predicate<? super T> predicate) {
            return stream.anyMatch(predicate);
        }

        @Override
        public boolean allMatch(Predicate<? super T> predicate) {
            return stream.allMatch(predicate);
        }

        @Override
        public boolean noneMatch(Predicate<? super T> predicate) {
            return stream.noneMatch(predicate);
        }

        @Override
        public Optional<T> findFirst() {
            return stream.findFirst();
        }

        @Override
        public Optional<T> findAny() {
            return stream.findAny();
        }

        @Override
        public Iterator<T> iterator() {
            return stream.iterator();
        }

        @Override
        public Spliterator<T> spliterator() {
            return stream.spliterator();
        }

        @Override
        public boolean isParallel() {
            return stream.isParallel();
        }

        @Override
        public StreamWrapper<T> sequential() {
            this.stream = stream.sequential();
            return this;
        }

        @Override
        public StreamWrapper<T> parallel() {
            this.stream = stream.parallel();
            return this;
        }

        @Override
        public StreamWrapper<T> unordered() {
            this.stream = stream.unordered();
            return this;
        }

        @Override
        public StreamWrapper<T> onClose(Runnable closeHandler) {
            this.stream = stream.onClose(closeHandler);
            return this;
        }

        @Override
        public void close() {
            stream.close();
        }
    }
}
