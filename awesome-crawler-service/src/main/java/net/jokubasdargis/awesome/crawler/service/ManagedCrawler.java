package net.jokubasdargis.awesome.crawler.service;

import com.google.common.collect.ImmutableList;

import net.jokubasdargis.awesome.crawler.CrawlStats;
import net.jokubasdargis.awesome.crawler.Crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.dropwizard.lifecycle.Managed;

import static java.util.Objects.requireNonNull;

final class ManagedCrawler implements Managed {

    static ManagedCrawler create(CrawlerFactory crawlerFactory,
                                 ScheduledExecutorService executorService) {
        return create(crawlerFactory, executorService, DEFAULT_CRAWL_STEP_DELAY);
    }

    static ManagedCrawler create(CrawlerFactory crawlerFactory,
                                 ScheduledExecutorService executorService,
                                 Duration crawlStepDelay) {
        return new ManagedCrawler(crawlerFactory, executorService, crawlStepDelay);
    }

    private static final Duration DEFAULT_CRAWL_STEP_DELAY = Duration.of(1, ChronoUnit.SECONDS);
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedCrawler.class);

    private final CrawlerFactory crawlerFactory;
    private final ScheduledExecutorService executorService;
    private final Duration crawlStepDelay;

    private ManagedCrawler(CrawlerFactory crawlerFactory,
                           ScheduledExecutorService executorService,
                           Duration crawlStepDelay) {
        this.crawlerFactory = requireNonNull(crawlerFactory);
        this.executorService = requireNonNull(executorService);
        this.crawlStepDelay = requireNonNull(crawlStepDelay);
    }

    @Override
    public void start() throws Exception {
        executorService.scheduleAtFixedRate(() -> {
            ScheduledFuture<List<CrawlStats>> job = executorService.schedule(
                    createCrawlTask(), 0, TimeUnit.SECONDS);
            executorService.schedule(() -> job.cancel(true), 30, TimeUnit.SECONDS);
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void stop() throws Exception {
        // We expect the executorService to be managed from outside, no need to shut it down here.
    }

    @SuppressWarnings("WhileLoopReplaceableByForEach")
    private Callable<List<CrawlStats>> createCrawlTask() {
        return () -> {
            LOGGER.info("Crawler task start");
            Crawler crawler = crawlerFactory.create();
            ImmutableList.Builder<CrawlStats> builder = ImmutableList.builder();
            Iterator<CrawlStats> iterator = crawler.iterator();
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (iterator.hasNext()) {
                        CrawlStats stats = iterator.next();
                        if (!stats.isSuccess()) {
                            // TODO(eleventigers, 24/03/16): handle crawl fail
                        }
                        iterator.remove();
                        builder.add(stats);
                    }
                    TimeUnit.MILLISECONDS.sleep(crawlStepDelay.toMillis());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return builder.build();
        };
    }
}
