package com.example.adoptions.adoptions;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Configuration
@Import(AdoptionsBeanRegistrar.class)
class Adoptions {

//    @Bean
//    YouIncompleteMeRunner youIncompleteMeRunner(IncompleteEventPublications eventPublications) {
//        return new YouIncompleteMeRunner(eventPublications);
//    }
}

class AdoptionsBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(@NonNull BeanRegistry registry, @NonNull Environment env) {

//        registry.registerBean(YouIncompleteMeRunner.class);

        registry.registerBean(YouIncompleteMeRunner.class, spec ->
                spec.supplier(supplierContext -> {
                    var iep = supplierContext.bean(IncompleteEventPublications.class);
                    return new YouIncompleteMeRunner(iep);
                })
        );
    }
}


class YouIncompleteMeRunner implements ApplicationRunner {

    private final IncompleteEventPublications eventPublications;

    YouIncompleteMeRunner(IncompleteEventPublications eventPublications) {
        this.eventPublications = eventPublications;
        IO.println("YouIncompleteMeRunner");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.eventPublications
                .resubmitIncompletePublications(e -> true);
    }
}

// look mom, no Lombok!!
record Dog(@Id int id, String name, String owner, String description) {
}

interface DogRepository extends ListCrudRepository<@NonNull Dog, @NonNull Long> {
}


@Controller
@ResponseBody
class DogsController {

    private final DogRepository dogRepository;

    DogsController(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @GetMapping(value = "/dogs", version = "1.0")
    Collection<Map<String, Object>> dogs() {
        return this.dogRepository.findAll()
                .stream()
                .map(d -> Map.of("id", (Object) d.id(), "name", (Object) d.name()))
                .toList();
    }

    @GetMapping(value = "/dogs", version = "1.1")
    Collection<Dog> dogsv2() {
        return this.dogRepository.findAll();
    }
}

@Service
@Transactional
class AdoptionsService {

    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    AdoptionsService(DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    void adopt(long dogId, String owner) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var updated = this.repository.save(new Dog(dog.id(),
                    dog.name(), owner, dog.description()));
            IO.println("adopted " + updated);
            this.applicationEventPublisher.publishEvent(new DogAdoptedEvent(dogId));
        });
    }
}

@Controller
@ResponseBody
class AdoptionsController {

    private final AdoptionsService service;

    AdoptionsController(AdoptionsService service) {
        this.service = service;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable long dogId, @RequestParam String owner) {
        this.service.adopt(dogId, owner);
    }
}