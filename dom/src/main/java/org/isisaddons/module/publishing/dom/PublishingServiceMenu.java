/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.isisaddons.module.publishing.dom;

import java.util.List;
import java.util.UUID;
import org.isisaddons.module.publishing.PublishingModule;
import org.joda.time.LocalDate;
import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Activity",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "40"
)
public class PublishingServiceMenu extends AbstractService {

    public static abstract class ActionDomainEvent extends PublishingModule.ActionDomainEvent<PublishingServiceMenu> {

        public ActionDomainEvent(final PublishingServiceMenu source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }


    // //////////////////////////////////////

    public static class QueuedPublishedEventsDomainEvent extends ActionDomainEvent {
        public QueuedPublishedEventsDomainEvent(final PublishingServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = QueuedPublishedEventsDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence="10")
    public List<PublishedEvent> queuedPublishedEvents() {
        return publishingServiceRepository.findQueued();
    }
    public boolean hideQueuedPublishedEvents() {
        return publishingServiceRepository == null;
    }

    // //////////////////////////////////////

    public static class FindPublishedEventsByTransactionIdDomainEvent extends ActionDomainEvent {
        public FindPublishedEventsByTransactionIdDomainEvent(final PublishingServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = FindPublishedEventsByTransactionIdDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            cssClassFa = "fa-crosshairs"
    )
    @MemberOrder(sequence="20")
    public List<PublishedEvent> findPublishedEventsByTransactionId(UUID transactionId) {
        return publishingServiceRepository.findByTransactionId(transactionId);
    }


    // //////////////////////////////////////

    public static class FindPublishedEventsDomainEvent extends ActionDomainEvent {
        public FindPublishedEventsDomainEvent(final PublishingServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = FindPublishedEventsDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            cssClassFa = "fa-search"
    )
    @MemberOrder(sequence="30")
    public List<PublishedEvent> findPublishedEvents(
            @Parameter(optionality= Optionality.OPTIONAL)
            @ParameterLayout(named="From")
            final LocalDate from,
            @Parameter(optionality= Optionality.OPTIONAL)
            @ParameterLayout(named="To")
            final LocalDate to) {
        return publishingServiceRepository.findByFromAndTo(from, to);
    }
    public boolean hideFindPublishedEvents() {
        return publishingServiceRepository == null;
    }
    public LocalDate default0FindPublishedEvents() {
        return clockService.now().minusDays(7);
    }
    public LocalDate default1FindPublishedEvents() {
        return clockService.now();
    }

    // //////////////////////////////////////


    public static class PurgeProcessedEventsDomainEvent extends ActionDomainEvent {
        public PurgeProcessedEventsDomainEvent(final PublishingServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = PurgeProcessedEventsDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-trash"
    )
    @MemberOrder(sequence="40")
    public void purgeProcessedEvents() {
        publishingServiceRepository.purgeProcessed();
    }
    public boolean hidePurgeProcessedEvents() {
        return publishingServiceRepository == null;
    }

    // //////////////////////////////////////

    public static class AllProcessedEventsDomainEvent extends ActionDomainEvent {
        public AllProcessedEventsDomainEvent(final PublishingServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllProcessedEventsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence="50")
    public List<PublishedEvent> allProcessedEvents() {
        return publishingServiceRepository.findProcessed();
    }
    public boolean hideAllProcessedEvents() {
        return publishingServiceRepository == null;
    }


    // //////////////////////////////////////

    @javax.inject.Inject
    private PublishingServiceRepository publishingServiceRepository;

    @javax.inject.Inject
    private ClockService clockService;

}

