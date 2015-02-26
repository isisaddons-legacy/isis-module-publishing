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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.inject.Named;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.VersionStrategy;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.PublishingChangeKind;
import org.apache.isis.applib.annotation.PublishingPayloadFactoryForAction;
import org.apache.isis.applib.annotation.PublishingPayloadFactoryForObject;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
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
@DomainObject(
        objectType = "PUBLISHEDCUSTOMER",
        publishing = Publishing.ENABLED,
        publishingPayloadFactory = PublishedCustomer.ObjectChangedEventPayloadFactory.class
)
public class PublishedCustomer implements Comparable<PublishedCustomer> {

    public static class ObjectChangedEventPayloadFactory implements PublishingPayloadFactoryForObject {
        @Override
        public EventPayload payloadFor(final Object changedObject, final PublishingChangeKind publishingChangeKind) {
            return new PublishedCustomerPayload((PublishedCustomer) changedObject);
        }

        public static class PublishedCustomerPayload
                extends EventPayloadForObjectChanged<PublishedCustomer> {

            public PublishedCustomerPayload(final PublishedCustomer changed) {
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
    }

    public static class UpdateAddressEventPayloadFactory implements PublishingPayloadFactoryForAction {

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
        public EventPayload payloadFor(final Identifier actionIdentifier, final Object target, final List<Object> arguments, final Object result) {
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
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            publishing = Publishing.ENABLED,
            publishingPayloadFactory = PublishedCustomer.UpdateAddressEventPayloadFactory.class
    )
    public PublishedCustomer updateAddress(
            @ParameterLayout(named="Line 1")
            final String line1,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Line 2")
            final String line2,
            @ParameterLayout(named="Town")
            final String town) {
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
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
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
    private SortedSet<ReferencedOrder> orders = new TreeSet<>();

    @MemberOrder(sequence = "1")
    public SortedSet<ReferencedOrder> getOrders() {
        return orders;
    }

    public void setOrders(final SortedSet<ReferencedOrder> orders) {
        this.orders = orders;
    }
    //endregion

    //region > addOrder (action)
    @Action(
            publishing = Publishing.ENABLED // using the default payload factory
    )
    public PublishedCustomer addOrder(
            @ParameterLayout(named="Name")
            final String name) {
        final ReferencedOrder order = container.newTransientInstance(ReferencedOrder.class);
        order.setName(name);
        getOrders().add(order);
        container.persistIfNotAlready(order);
        return this;
    }
    //endregion

    //region > removeOrder (action)
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
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
    public int compareTo(final PublishedCustomer other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private DomainObjectContainer container;

    //endregion

}
