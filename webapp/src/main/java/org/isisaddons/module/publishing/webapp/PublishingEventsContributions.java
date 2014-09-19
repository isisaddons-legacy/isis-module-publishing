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
package org.isisaddons.module.publishing.webapp;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.publishing.dom.PublishedEvent;
import org.isisaddons.module.publishing.dom.PublishingServiceRepository;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomer;
import org.isisaddons.module.publishing.fixture.dom.ReferencedAddress;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

@DomainService(menuOrder = "90")
public class PublishingEventsContributions {

    @NotInServiceMenu
    @NotContributed(NotContributed.As.ACTION)// ie contributed as collection
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> publishedEvents(
            final PublishedCustomer publishedCustomer) {
        return publishedEventsFor(publishedCustomer);
    }

    @NotInServiceMenu
    @NotContributed(NotContributed.As.ACTION)// ie contributed as collection
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> publishedEvents(
            final ReferencedAddress referencedAddress) {
        return publishedEventsFor(referencedAddress);
    }

    private List<PublishedEvent> publishedEventsFor(Object domainObject) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(domainObject);
        return publishingServiceRepository.findByTargetAndFromAndTo(bookmark, null, null);
    }

    @NotInServiceMenu
    @NotContributed(NotContributed.As.ASSOCIATION)// ie contributed as action
    public PublishedCustomer purgeEvents(
            final PublishedCustomer publishedCustomer) {
        final List<PublishedEvent> publishedEvents = publishedEvents(publishedCustomer);
        for (PublishedEvent publishedEvent : publishedEvents) {
            container.removeIfNotAlready(publishedEvent);
        }
        return publishedCustomer;
    }


    @Inject
    private DomainObjectContainer container;
    @Inject
    private PublishingServiceRepository publishingServiceRepository;
    @Inject
    private BookmarkService bookmarkService;


}
