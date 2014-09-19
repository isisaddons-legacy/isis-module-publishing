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

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.services.HasTransactionId;


public class PublishingServiceContributions extends AbstractFactoryAndRepository {

    @ActionSemantics(Of.SAFE)
    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @Render(Type.EAGERLY)
    public List<PublishedEvent> publishedEvents(final HasTransactionId hasTransactionId) {
        return publishedEventRepository.findByTransactionId(hasTransactionId.getTransactionId());
    }
    
    // //////////////////////////////////////

    @javax.inject.Inject
    private PublishingServiceRepository publishedEventRepository;

}