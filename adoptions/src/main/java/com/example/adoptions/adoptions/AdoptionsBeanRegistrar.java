package com.example.adoptions.adoptions;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.core.env.Environment;
import org.springframework.modulith.events.IncompleteEventPublications;

public class AdoptionsBeanRegistrar implements BeanRegistrar {

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
