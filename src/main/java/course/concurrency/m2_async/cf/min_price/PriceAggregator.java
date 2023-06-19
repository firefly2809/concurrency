package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
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
        List<CompletableFuture<Double>> getMinPriceFutures = shopIds.stream().map(shopId ->
                CompletableFuture
                        .supplyAsync(
                                () -> priceRetriever.getPrice(itemId, shopId),
                                executorService
                        )
                        .exceptionally(exception -> null)
                        .completeOnTimeout(null, 2950, TimeUnit.MILLISECONDS)
        ).collect(Collectors.toList());

        return CompletableFuture.allOf(getMinPriceFutures.toArray(CompletableFuture[]::new))
                .thenApply(v -> getMinPriceFutures.stream().map(CompletableFuture::join))
                .thenApply(priceStream -> priceStream
                        .filter(Objects::nonNull)
                        .min(Comparator.naturalOrder())
                )
                .join()
                .orElse(Double.NaN);
    }
}
