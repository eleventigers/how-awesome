package net.jokubasdargis.awesome.crawler.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;

import static java.util.Objects.requireNonNull;

public final class CrawlerConfiguration extends Configuration {

    @Valid
    @NotNull
    private MessageRouterFactory messageRouterFactory = new MessageRouterFactory();

    @Valid
    @NotNull
    private LinkFrontierFactory linkFrontierFactory = new LinkFrontierFactory();

    @JsonProperty("messageRouter")
    public MessageRouterFactory getMessageRouterFactory() {
        return messageRouterFactory;
    }

    @JsonProperty("messageRouter")
    public void setMessageRouterFactory(MessageRouterFactory factory) {
        this.messageRouterFactory = requireNonNull(factory);
    }

    @JsonProperty("linkFrontier")
    public LinkFrontierFactory getLinkFrontierFactory() {
        return linkFrontierFactory;
    }

    @JsonProperty("linkFrontier")
    public void setLinkFrontierFactory(LinkFrontierFactory factory) {
        this.linkFrontierFactory = factory;
    }
}
