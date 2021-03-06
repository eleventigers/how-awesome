package net.jokubasdargis.awesome.aggregator.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.jokubasdargis.awesome.message.MessageRouter;
import net.jokubasdargis.awesome.message.MessageRouters;

import java.net.URI;

import javax.validation.constraints.NotNull;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;

final class MessageRouterFactory {

    @NotNull
    private URI uri;

    @JsonProperty
    public URI getUri() {
        return uri;
    }

    @JsonProperty
    public void setUri(URI uri) {
        this.uri = uri;
    }

    MessageRouter build(Environment environment) {
        MessageRouter messageRouter = MessageRouters.awesome(uri);
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
            }

            @Override
            public void stop() throws Exception {
                messageRouter.close();
            }
        });
        return messageRouter;
    }
}
