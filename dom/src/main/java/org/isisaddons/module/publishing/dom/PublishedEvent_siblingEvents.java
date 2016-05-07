/*
 *  Copyright 2016 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.publishing.dom;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.publishing.PublishingModule;

@Mixin
public class PublishedEvent_siblingEvents {


    public static class ActionDomainEvent
            extends PublishingModule.ActionDomainEvent<PublishedEvent_siblingEvents> { }


    private final PublishedEvent publishedEvent;

    public PublishedEvent_siblingEvents(final PublishedEvent publishedEvent) {
        this.publishedEvent = publishedEvent;
    }

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(
            defaultView = "table"
    )
    @MemberOrder(sequence = "100.110")
    public List<PublishedEvent> $$() {
        final List<PublishedEvent> eventList = publishingServiceRepository
                .findByTransactionId(publishedEvent.getTransactionId());
        eventList.remove(publishedEvent);
        return eventList;
    }

    @javax.inject.Inject
    private PublishingServiceRepository publishingServiceRepository;
    
}
