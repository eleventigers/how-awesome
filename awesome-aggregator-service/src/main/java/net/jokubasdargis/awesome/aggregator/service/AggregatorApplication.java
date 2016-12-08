package net.jokubasdargis.awesome.aggregator.service;

import net.jokubasdargis.awesome.message.MessageRouter;

import java.util.concurrent.ScheduledExecutorService;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public final class AggregatorApplication extends Application<AggregatorConfiguration> {

    public static void main(String... args) throws Exception {
        new AggregatorApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<AggregatorConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(
            AggregatorConfiguration configuration, Environment environment) throws Exception {
        MessageRouter messageRouter = configuration.getMessageRouterFactory().build(environment);

        ScheduledExecutorService executor = environment.lifecycle()
                .scheduledExecutorService("Logger %d").threads(8).build();
        ManagedMessageLogger managedMessageLogger = ManagedMessageLogger
                .create(messageRouter, executor);
        environment.lifecycle().manage(managedMessageLogger);
    }
}
