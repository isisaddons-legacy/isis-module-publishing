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
package org.isisaddons.module.publishing.integtests;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.publishing.dom.PublishedEvent;
import org.isisaddons.module.publishing.dom.PublishingServiceRepository;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomer;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomers;
import org.isisaddons.module.publishing.fixture.scripts.PublishingEventSerializerModuleAppSetUpFixture;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.publish.EventType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class PublishingServiceTest extends PublishingEventSerializerModuleIntegTest {

    PublishedCustomer publishedCustomerWO;
    // work-around, currently unable to unwrap domain objects...
    PublishedCustomer publishedCustomer;

    @Before
    public void setUp() throws Exception {
        scenarioExecution().install(new PublishingEventSerializerModuleAppSetUpFixture());
        isisJdoSupport.deleteAll(PublishedEvent.class);

        assertThat(publishingServiceRepository.findQueued(), is(empty()));
        assertThat(publishingServiceRepository.findProcessed(), is(empty()));

        nextTransaction();

        // user
        final List<PublishedCustomer> all = wrap(publishedCustomers).listAll();
        assertThat(all.size(), is(3));

        publishedCustomer = all.get(0);
        publishedCustomerWO = wrap(publishedCustomer);
        assertThat(publishedCustomerWO.getName(), is("Mary"));
    }


    public static class ActionInvocation extends PublishingServiceTest {

        @Ignore // can't test yet because Command#executor is OTHER rather than USER
        @Test
        public void happyCase() throws Exception {

            // when
            publishedCustomerWO.updateAddress("45 Main Street", "Middletown", "Trumpton");
            nextTransaction();

            // then
            final List<PublishedEvent> events = publishingServiceRepository.findQueued();

            assertThat(events, is(not(empty())));
            final PublishedEvent publishedEvent = events.get(0);
            assertThat(publishedEvent.getEventType(), is(EventType.ACTION_INVOCATION));
            assertThat(publishedEvent.getTargetStr(), is("PUBLISHEDCUSTOMER:L_0"));
        }


    }

    public static class ObjectChanged extends PublishingServiceTest {

        public static class OnCreate extends ObjectChanged {

            @Test
            public void happyCase() throws Exception {

                // when
                final PublishedCustomer newCustomer = publishedCustomers.create("Faz");
                nextTransaction();

                // then
                final List<PublishedEvent> customerEvents = publishingServiceRepository.findByTargetAndFromAndTo(
                        bookmarkFor(newCustomer), null, null);

                assertThat(customerEvents, is(not(empty())));
                final PublishedEvent publishedEvent = customerEvents.get(0);
                assertThat(publishedEvent.getEventType(), is(EventType.OBJECT_CREATED));

            }

        }

        public static class OnUpdate extends ObjectChanged {

            @Test
            public void happyCase() throws Exception {

                // when
                publishedCustomerWO.setName("Mary-Anne");
                nextTransaction();

                // then
                final List<PublishedEvent> events = publishingServiceRepository.findByTargetAndFromAndTo(
                        bookmarkFor(publishedCustomerWO), null, null);

                assertThat(events, is(not(empty())));
                final PublishedEvent publishedEvent = events.get(0);
                assertThat(publishedEvent.getEventType(), is(EventType.OBJECT_UPDATED));
            }
        }
    }



    Bookmark bookmarkFor(Object domainObject) {
        return bookmarkService.bookmarkFor(domainObject);
    }

    @Inject
    PublishedCustomers publishedCustomers;
    @Inject
    PublishingServiceRepository publishingServiceRepository;
    @Inject
    IsisJdoSupport isisJdoSupport;
    @Inject
    BookmarkService bookmarkService;


}