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
package org.isisaddons.module.publishing.fixture.scripts;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

public class PublishingEventSerializerModuleAppTearDownFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"isispublishing\".\"PublishedEvent\"");

        isisJdoSupport.executeUpdate("delete from \"PublishedCustomer_orders\"");
        isisJdoSupport.executeUpdate("delete from \"ReferencedOrder\"");
        isisJdoSupport.executeUpdate("delete from \"PublishedCustomer\"");
        isisJdoSupport.executeUpdate("delete from \"ReferencedAddress\"");

        isisJdoSupport.executeUpdate("delete from \"SomeUnpublishedObject\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
