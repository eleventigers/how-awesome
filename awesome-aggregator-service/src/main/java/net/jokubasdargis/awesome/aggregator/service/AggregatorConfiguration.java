package net.jokubasdargis.awesome.aggregator.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;

import static java.util.Objects.requireNonNull;

final class AggregatorConfiguration extends Configuration {

    @Valid
    @NotNull
    private MessageRouterFactory messageRouterFactory = new MessageRouterFactory();

    @JsonProperty("messageRouter")
    public MessageRouterFactory getMessageRouterFactory() {
        return messageRouterFactory;
    }

    @JsonProperty("messageRouter")
    public void setMessageRouterFactory(MessageRouterFactory factory) {
        this.messageRouterFactory = requireNonNull(factory);
    }
}
