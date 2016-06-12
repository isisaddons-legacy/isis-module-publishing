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
package org.isisaddons.module.publishing.dom.eventserializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.adapter.oid.Oid;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;

class EventSerializerRendererContext implements RendererContext {

    private final String baseUrl;
    private final Where where;
    
    public EventSerializerRendererContext(String baseUrl, Where where) {
        this.baseUrl = baseUrl;
        this.where = where;
    }

    @Override
    public String urlFor(String url) {
        return baseUrl + url;
    }

    @Override
    public AuthenticationSession getAuthenticationSession() {
        return IsisContext.getSessionFactory().getCurrentSession().getAuthenticationSession();
    }

    @Override
    public PersistenceSession getPersistenceSession() {
        return IsisContext.getSessionFactory().getCurrentSession().getPersistenceSession();
    }

    @Override
    public IsisConfiguration getConfiguration() {
        return IsisContext.getSessionFactory().getConfiguration();
    }

    @Override
    public AdapterManager getAdapterManager() {
        return getPersistenceSession();
    }

    @Override
    public List<List<String>> getFollowLinks() {
        return Collections.emptyList();
    }

    @Override
    public Where getWhere() {
        return where;
    }

    @Override
    public boolean honorUiHints() {
        return false;
    }

    @Override
    public boolean objectPropertyValuesOnly() {
        return false;
    }

    @Override
    public boolean suppressDescribedByLinks() {
        return false;
    }

    @Override
    public boolean suppressUpdateLink() {
        return false;
    }

    @Override
    public boolean suppressMemberId() {
        return false;
    }

    @Override
    public boolean suppressMemberLinks() {
        return false;
    }

    @Override
    public boolean suppressMemberExtensions() {
        return false;
    }

    @Override
    public boolean suppressMemberDisabledReason() {
        return false;
    }

    private Set<Oid> rendered = Sets.newHashSet();
    @Override
    public boolean canEagerlyRender(ObjectAdapter objectAdapter) {
        final Oid oid = objectAdapter.getOid();
        return rendered.add(oid);
    }

}
