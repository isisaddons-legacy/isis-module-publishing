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
package org.isisaddons.module.publishing.fixture.dom;

import java.util.List;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;

@Named("Customers")
@DomainService(menuOrder = "10", repositoryFor = PublishedCustomer.class)
public class PublishedCustomers {

    //region > identification in the UI

    public String getId() {
        return "publishedCustomers";
    }

    public String iconName() {
        return "PublishedCustomers";
    }

    //endregion

    //region > listAll (action)

    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<PublishedCustomer> listAll() {
        return container.allInstances(PublishedCustomer.class);
    }

    //endregion

    //region > create (action)

    @MemberOrder(sequence = "2")
    public PublishedCustomer create(
            final @Named("Name") String name) {
        final PublishedCustomer obj = container.newTransientInstance(PublishedCustomer.class);
        obj.setName(name);
        container.persistIfNotAlready(obj);
        return obj;
    }

    //endregion

    //region > injected services

    @javax.inject.Inject 
    DomainObjectContainer container;

    //endregion

}
