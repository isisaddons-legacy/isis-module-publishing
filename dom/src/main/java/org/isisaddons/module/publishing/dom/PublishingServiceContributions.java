/*
 *  Copyright 2013~2014 Dan Haywood
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
import org.isisaddons.module.publishing.PublishingModule;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.HasTransactionId;


@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class PublishingServiceContributions extends AbstractFactoryAndRepository {

    public static abstract class CollectionDomainEvent<T> extends PublishingModule.CollectionDomainEvent<PublishingServiceContributions,T> {
        public CollectionDomainEvent(final PublishingServiceContributions source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of) {
            super(source, identifier, of);
        }
        public CollectionDomainEvent(final PublishingServiceContributions source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    // //////////////////////////////////////

    public static class PublishedEventsDomainEvent extends CollectionDomainEvent<PublishedEvent> {
        public PublishedEventsDomainEvent(final PublishingServiceContributions source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of) {
            super(source, identifier, of);
        }
        public PublishedEventsDomainEvent(final PublishingServiceContributions source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of, final PublishedEvent value) {
            super(source, identifier, of, value);
        }
    }

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @Collection(
            domainEvent = PublishedEventsDomainEvent.class
    )
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    public List<PublishedEvent> publishedEvents(final HasTransactionId hasTransactionId) {
        return publishedEventRepository.findByTransactionId(hasTransactionId.getTransactionId());
    }
    
    // //////////////////////////////////////

    @javax.inject.Inject
    private PublishingServiceRepository publishedEventRepository;

}
