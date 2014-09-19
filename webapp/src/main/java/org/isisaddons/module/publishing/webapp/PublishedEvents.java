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
import java.util.UUID;
import javax.inject.Inject;
import org.isisaddons.module.publishing.dom.PublishedEvent;
import org.isisaddons.module.publishing.dom.PublishingServiceRepository;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomer;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

@DomainService(menuOrder = "20")
public class PublishedEvents {

    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> findQueued() {
        return publishingServiceRepository.findQueued();
    }

    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> findProcessed() {
        return publishingServiceRepository.findProcessed();
    }

    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> findByTransactionId(UUID transactionId) {
        return publishingServiceRepository.findByTransactionId(transactionId);
    }

    @NotContributed
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> findByTargetAndFromAndTo(PublishedCustomer publishedCustomer, LocalDate from, LocalDate to) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(publishedCustomer);
        return publishingServiceRepository.findByTargetAndFromAndTo(bookmark, from, to);
    }

    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<PublishedEvent> findByFromAndTo(LocalDate from, LocalDate to) {
        return publishingServiceRepository.findByFromAndTo(from, to);
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public void purgeProcessed() {
        publishingServiceRepository.purgeProcessed();
    }


    @Inject
    private PublishingServiceRepository publishingServiceRepository;
    @Inject
    private BookmarkService bookmarkService;


}