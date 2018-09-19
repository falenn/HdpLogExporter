package org.talants.hdp.clients;

import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import java.util.List;

import org.talants.hdp.model.github.Contributor;

public interface Github {
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(@Param("owner") String Owner, @Param("repo") String repo);

    static Github connect() {
        return Feign.builder()
            .decoder(new GsonDecoder())
            .logger(new Logger.JavaLogger())
            .logLevel(Logger.Level.FULL)
            .target(Github.class, "https://api.github.com");
    }
}

