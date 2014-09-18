/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

package org.isisaddons.module.publishing.fixture.scripts;

import org.isisaddons.module.publishing.fixture.dom.PublishedCustomer;
import org.isisaddons.module.publishing.fixture.dom.PublishedCustomers;
import org.isisaddons.module.publishing.fixture.dom.SomeUnpublishedObject;
import org.isisaddons.module.publishing.fixture.dom.SomeUnpublishedObjects;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class PublishingEventSerializerModuleAppSetUpFixture extends DiscoverableFixtureScript {

    public PublishingEventSerializerModuleAppSetUpFixture() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        execute(new PublishingEventSerializerModuleAppTearDownFixture(), executionContext);

        // create customers
        final PublishedCustomer mary = createCustomer("Mary", executionContext);
        mary.updateAddress("23 High Street", null, "Camberwick Green");
        mary.addOrder("Nintendo Wii");
        mary.addOrder("Raspberry Pi");
        mary.addOrder("Google Glass");

        final PublishedCustomer mungo = createCustomer("Mungo", executionContext);
        mungo.updateAddress("45 Main Street", "Chiswick", "London");

        final PublishedCustomer midge = createCustomer("Midge", executionContext);
        midge.addOrder("Bread");
        midge.addOrder("Cheese");
        midge.addOrder("Milk");


        // create the 'unpublished object'
        createUnpublishedObject("Foo", executionContext);
        createUnpublishedObject("Bar", executionContext);
        createUnpublishedObject("Bop", executionContext);

    }

    // //////////////////////////////////////

    private PublishedCustomer createCustomer(final String name, ExecutionContext executionContext) {
        return executionContext.add(this, publishedCustomers.create(name));
    }

    private SomeUnpublishedObject createUnpublishedObject(final String name, ExecutionContext executionContext) {
        return executionContext.add(this, someUnpublishedObjects.create(name));
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private PublishedCustomers publishedCustomers;

    @javax.inject.Inject
    private SomeUnpublishedObjects someUnpublishedObjects;

}
