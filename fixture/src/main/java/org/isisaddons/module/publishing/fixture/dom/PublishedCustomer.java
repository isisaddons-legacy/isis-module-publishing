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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.jdo.annotations.*;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.publish.EventPayload;
import org.apache.isis.applib.services.publish.EventPayloadForActionInvocation;
import org.apache.isis.applib.services.publish.EventPayloadForObjectChanged;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@ObjectType("PUBLISHEDCUSTOMER")
@Bookmarkable
@PublishedObject(value = PublishedCustomer.ObjectChangedEventPayloadFactory.class)
public class PublishedCustomer implements Comparable<PublishedCustomer> {

    public static class ObjectChangedEventPayloadFactory implements PublishedObject.PayloadFactory {
        public static class PublishedCustomerPayload
                extends EventPayloadForObjectChanged<PublishedCustomer> {

            public PublishedCustomerPayload(PublishedCustomer changed) {
                super(changed);
            }

            public String getAddressTown() {
                final ReferencedAddress address = getChanged().getAddress();
                return address != null? address.getTown(): null;
            }

            public String getCustomerName() {
                return getChanged().getName();
            }

            public SortedSet<ReferencedOrder> getOrders() {
                return getChanged().getOrders();
            }
        }

        @Override
        public EventPayload payloadFor(Object changedObject, PublishedObject.ChangeKind changeKind) {
            return new PublishedCustomerPayload((PublishedCustomer) changedObject);
        }
    }

    public static class UpdateAddressEventPayloadFactory implements PublishedAction.PayloadFactory {

        public static class UpdateAddressEventPayload
                extends EventPayloadForActionInvocation<PublishedCustomer> {

            public UpdateAddressEventPayload(
                    final Identifier actionIdentifier,
                    final PublishedCustomer target,
                    final List<?> arguments,
                    final Object result) {
                super(actionIdentifier, target, arguments, result);
            }
        }

        @Override
        public EventPayload payloadFor(Identifier actionIdentifier, Object target, List<Object> arguments, Object result) {
            return new UpdateAddressEventPayload(actionIdentifier, (PublishedCustomer) target, arguments, result);
        }
    }

    //region > name (property)

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    //endregion

    //region > address (property)
    private ReferencedAddress address;

    @Column(allowsNull = "true")
    public ReferencedAddress getAddress() {
        return address;
    }

    public void setAddress(final ReferencedAddress address) {
        this.address = address;
    }

    //endregion

    //region > updateAddress (published action), clearAddress (action)
    @PublishedAction(value = PublishedCustomer.UpdateAddressEventPayloadFactory.class)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public PublishedCustomer updateAddress(
            final @Named("Line 1") String line1,
            final @Named("Line 2") @Optional String line2,
            final @Named("Town") String town) {
        ReferencedAddress address = getAddress();
        if(address == null) {
            address = container.newTransientInstance(ReferencedAddress.class);
        }
        address.setLine1(line1);
        address.setLine2(line2);
        address.setTown(town);
        setAddress(address);
        container.persistIfNotAlready(address);
        return this;
    }

    public String default0UpdateAddress() {
        return getAddress() != null? getAddress().getLine1(): null;
    }
    public String default1UpdateAddress() {
        return getAddress() != null? getAddress().getLine2(): null;
    }
    public String default2UpdateAddress() {
        return getAddress() != null? getAddress().getTown(): null;
    }

    @Named("Clear")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public PublishedCustomer resetAddress(
            final @Named("Are you sure?") boolean areYouSure) {
        setAddress(null);
        return this;
    }

    public String validateResetAddress(final boolean areYouSure) {
        return !areYouSure? "Check 'are you sure' to proceed": null;
    }

    //endregion

    //region > orders (collection)
    @Join
    @Element(dependent = "false")
    private SortedSet<ReferencedOrder> orders = new TreeSet<ReferencedOrder>();

    @MemberOrder(sequence = "1")
    public SortedSet<ReferencedOrder> getOrders() {
        return orders;
    }

    public void setOrders(final SortedSet<ReferencedOrder> orders) {
        this.orders = orders;
    }
    //endregion

    //region > addOrder (action)
    @PublishedAction // using the default payload factory
    public PublishedCustomer addOrder(final @Named("Name") String name) {
        final ReferencedOrder order = container.newTransientInstance(ReferencedOrder.class);
        order.setName(name);
        getOrders().add(order);
        container.persistIfNotAlready(order);
        return this;
    }
    //endregion

    //region > removeOrder (action)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public PublishedCustomer removeOrder(final Order order) {
        getOrders().remove(order);
        container.removeIfNotAlready(order);
        return this;
    }

    public Collection<ReferencedOrder> choices0RemoveOrder() {
        return getOrders();
    }
    //endregion
    
    //region > compareTo

    @Override
    public int compareTo(PublishedCustomer other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private DomainObjectContainer container;

    //endregion

}
