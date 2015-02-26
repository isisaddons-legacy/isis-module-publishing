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
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = SomeUnpublishedObject.class
)
public class SomeUnpublishedObjects {

    //region > identification in the UI

    public String getId() {
        return "SomeUnpublishedObject";
    }

    public String iconName() {
        return "SomeUnpublishedObject";
    }

    //endregion

    //region > listAll (action)

    public List<SomeUnpublishedObject> listAll() {
        return container.allInstances(SomeUnpublishedObject.class);
    }

    //endregion

    //region > create (action)

    public SomeUnpublishedObject create(
            final String name) {
        final SomeUnpublishedObject obj = container.newTransientInstance(SomeUnpublishedObject.class);
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
