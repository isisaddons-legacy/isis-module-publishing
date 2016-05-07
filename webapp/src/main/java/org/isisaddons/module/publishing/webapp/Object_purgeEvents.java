package org.isisaddons.module.publishing.webapp;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.publishing.dom.PublishedEvent;
import org.isisaddons.module.publishing.dom.PublishingServiceRepository;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomer;
import org.isisaddons.module.publishing.fixture.dom.ReferencedAddress;

@Mixin
public class Object_purgeEvents {

    private final Object object;

    public Object_purgeEvents(Object object) {
        this.object = object;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    public Object $$() {
        final List<PublishedEvent> publishedEvents = publishingServiceRepository
                .findByTargetAndFromAndTo(object, null, null);
        for (final PublishedEvent publishedEvent : publishedEvents) {
            repositoryService.remove(publishedEvent);
        }
        return object;
    }
    public boolean hide$$() {
        return !(this.object instanceof PublishedCustomer || this.object instanceof ReferencedAddress);
    }


    @Inject
    private RepositoryService repositoryService;
    @Inject
    private PublishingServiceRepository publishingServiceRepository;

}
