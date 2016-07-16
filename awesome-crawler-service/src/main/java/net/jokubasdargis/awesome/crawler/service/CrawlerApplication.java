package net.jokubasdargis.awesome.crawler.service;

import net.jokubasdargis.awesome.core.Link;
import net.jokubasdargis.awesome.core.Result;
import net.jokubasdargis.awesome.crawler.Crawlers;
import net.jokubasdargis.awesome.crawler.LinkFrontier;
import net.jokubasdargis.awesome.crawler.LinkResponse;
import net.jokubasdargis.awesome.crawler.OkHttpLinkFetcher;
import net.jokubasdargis.awesome.message.MessageRouter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import kotlin.jvm.functions.Function1;

public final class CrawlerApplication extends Application<CrawlerConfiguration> {

    public static void main(String... args) throws Exception {
        new CrawlerApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<CrawlerConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(CrawlerConfiguration configuration, Environment environment) throws Exception {
        LinkFrontier linkFrontier = configuration.getLinkFrontierFactory().build(environment);
        linkFrontier.add(Crawlers.awesomeRootLink());

        MessageRouter messageRouter = configuration.getMessageRouterFactory().build(environment);

        Function1<Link, Result<LinkResponse>> linkFetcher = OkHttpLinkFetcher.create();

        CrawlerFactory crawlerFactory = () -> Crawlers
                .newAwesomeCrawler(linkFrontier, linkFetcher, messageRouter);

        ScheduledExecutorService executor = environment.lifecycle()
                .scheduledExecutorService("Crawler %d").threads(2).build();
        ManagedCrawler crawlerManager = ManagedCrawler.create(crawlerFactory, executor);
        environment.lifecycle().manage(crawlerManager);
    }
}
