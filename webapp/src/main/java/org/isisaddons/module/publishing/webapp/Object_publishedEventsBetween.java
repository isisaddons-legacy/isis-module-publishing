package org.isisaddons.module.publishing.webapp;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.publishing.PublishingModule;
import org.isisaddons.module.publishing.dom.PublishedEvent;
import org.isisaddons.module.publishing.dom.PublishingServiceRepository;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomer;
import org.isisaddons.module.publishing.fixture.dom.ReferencedAddress;

@Mixin
public class Object_publishedEventsBetween {

    private final Object object;
    public Object_publishedEventsBetween(Object object) {
        this.object = object;
    }

    public static class ActionDomainEvent extends PublishingModule.ActionDomainEvent<Object> {
    }

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            named = "Find between",
            contributed = Contributed.AS_ACTION,
            cssClassFa = "fa-search"
    )
    @MemberOrder(name = "publishedEvents", sequence = "1")
    public List<PublishedEvent> $$() {
        return publishingServiceRepository.findByTargetAndFromAndTo(this.object, null, null);
    }

    public boolean hide$$() {
        return !(this.object instanceof PublishedCustomer || this.object instanceof ReferencedAddress);
    }

    @Inject
    private PublishingServiceRepository publishingServiceRepository;

}
