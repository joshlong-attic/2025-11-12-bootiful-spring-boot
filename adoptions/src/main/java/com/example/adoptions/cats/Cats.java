package com.example.adoptions.cats;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;

import java.util.Collection;

@Configuration
@ImportHttpServices(CatFactsClient.class)
class Cats {
}

@Controller
@ResponseBody
class CatFactsController {

    private final CatFactsClient client;

    CatFactsController(CatFactsClient client) {
        this.client = client;
    }

    @GetMapping("/cats/facts")
    Collection <CatFact> facts(){
        return this.client
                .facts()
                .facts();
    }
}

interface CatFactsClient {

    @GetExchange("https://www.catfacts.net/api")
    CatFacts facts();
}

record CatFact(String fact) {
}

record CatFacts(Collection<CatFact> facts) {
}