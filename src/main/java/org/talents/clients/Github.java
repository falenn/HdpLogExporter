package org.talents.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.List;

import org.talents.model.github.Contributor;

//https://dzone.com/articles/microservices-communication-feign-as-rest-client
//https://github.com/OpenFeign/feign/blob/master/example-github/src/main/java/feign/example/github/GitHubExample.java

public interface Github {
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(@Param("owner") String Owner, @Param("repo") String repo);

    ObjectMapper mapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.INDENT_OUTPUT, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static Github connect() {
        return Feign.builder()
            .decoder(new JacksonDecoder(mapper))
            .encoder(new JacksonEncoder(mapper))
            .logger(new Logger.JavaLogger())
            .logLevel(Logger.Level.FULL)
            .target(Github.class, "https://api.github.com");
    }

    //https://api.github.com/repos/falenn/k8sPlayground/contributors
}

