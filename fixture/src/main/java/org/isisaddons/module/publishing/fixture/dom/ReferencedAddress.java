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
package org.isisaddons.module.publishing.fixture.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@ObjectType("ADDRESS")
@Bookmarkable
@PublishedObject // using the default payload factory
public class ReferencedAddress implements Comparable<ReferencedAddress> {

    //region > line1 (property)

    private String line1;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    @MemberOrder(sequence="1")
    public String getLine1() {
        return line1;
    }

    public void setLine1(final String line1) {
        this.line1 = line1;
    }

    //endregion

    //region > line2 (property)

    private String line2;

    @javax.jdo.annotations.Column(allowsNull="true")
    @MemberOrder(sequence="2")
    public String getLine2() {
        return line2;
    }

    public void setLine2(final String line2) {
        this.line2 = line2;
    }

    //endregion

    //region > town (property)

    private String town;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(prepend = ",", sequence="2")
    @MemberOrder(sequence="3")
    public String getTown() {
        return town;
    }

    public void setTown(final String town) {
        this.town = town;
    }

    //endregion

    //region > compareTo

    @Override
    public int compareTo(ReferencedAddress other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private DomainObjectContainer container;

    //endregion

}
