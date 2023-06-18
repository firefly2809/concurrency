package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    /*
     * C ForkJoinPool.commonPool() тесты падают.
     * Добавил cached, так как неизвестно сколько магазинов будет на входе, так как нагрузка в основновном блокирующая
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        Double result = Double.NaN;
        NavigableSet<Double> minPriceSorted = new ConcurrentSkipListSet<>();

        List<CompletableFuture<Void>> getMinPriceFutures = shopIds.stream().map(shopId ->
                CompletableFuture
                        .supplyAsync(
                                () -> priceRetriever.getPrice(itemId, shopId),
                                executorService
                        )
                        .exceptionally(exception -> null)
                        .completeOnTimeout(null, 2950, TimeUnit.MILLISECONDS)
                        .thenAccept(minPrice -> {
                            if (minPrice != null)
                                minPriceSorted.add(minPrice);
                        })
        ).collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(getMinPriceFutures.toArray(CompletableFuture[]::new));
        allFutures.join();

        if (!minPriceSorted.isEmpty())
            result = minPriceSorted.first();

        return result;
    }
}
