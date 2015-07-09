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

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.jdo.annotations.IdentityType;
import org.isisaddons.module.publishing.PublishingModule;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.HasUsername;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.publish.EventType;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.objectstore.jdo.applib.service.DomainChangeJdoAbstract;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;
import org.apache.isis.objectstore.jdo.applib.service.Util;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.APPLICATION,
        schema = "isispublishing",
        table="PublishedEvent",
        objectIdClass=PublishedEventPK.class)
@javax.jdo.annotations.Queries( {
    @javax.jdo.annotations.Query(
            name="findByStateOrderByTimestamp", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE state == :state "
                    + "ORDER BY timestamp"),
    @javax.jdo.annotations.Query(
            name="findByTransactionId", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE transactionId == :transactionId"),
    @javax.jdo.annotations.Query(
            name="findByTargetAndTimestampBetween", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE targetStr == :targetStr " 
                    + "&& timestamp >= :from " 
                    + "&& timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="findByTargetAndTimestampAfter", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE targetStr == :targetStr " 
                    + "&& timestamp >= :from "
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="findByTargetAndTimestampBefore", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE targetStr == :targetStr " 
                    + "&& timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="findByTarget", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE targetStr == :targetStr " 
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="findByTimestampBetween", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE timestamp >= :from " 
                    + "&&    timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="findByTimestampAfter", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE timestamp >= :from "
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="findByTimestampBefore", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "WHERE timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @javax.jdo.annotations.Query(
            name="find", language="JDOQL",  
            value="SELECT "
                    + "FROM org.isisaddons.module.publishing.dom.PublishedEvent "
                    + "ORDER BY timestamp DESC")
})
@MemberGroupLayout(
        columnSpans={6,0,6},
        left={"Identifiers","Target"},
        right={"Detail","State"})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "isispublishing.PublishedEvent"
)
@DomainObjectLayout(
        named = "Published Event"
)
public class PublishedEvent extends DomainChangeJdoAbstract implements HasTransactionId, HasUsername {

    public static abstract class PropertyDomainEvent<T> extends PublishingModule.PropertyDomainEvent<PublishedEvent, T> {
        public PropertyDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final PublishedEvent source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends PublishingModule.CollectionDomainEvent<PublishedEvent, T> {
        public CollectionDomainEvent(final PublishedEvent source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final PublishedEvent source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends PublishingModule.ActionDomainEvent<PublishedEvent> {
        public ActionDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final PublishedEvent source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final PublishedEvent source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    public PublishedEvent() {
        super(ChangeType.PUBLISHED_EVENT);
    }

    // //////////////////////////////////////
    // Identification
    // //////////////////////////////////////

    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(getEventType().name()).append(" ").append(getTargetStr());
        if(getEventType()==EventType.ACTION_INVOCATION) {
            buf.append(" ").append(getMemberIdentifier());
        }
        buf.append(",").append(getState());
        return buf.toString();
    }


    // //////////////////////////////////////
    // user (property)
    // //////////////////////////////////////

    public static class UserDomainEvent extends PropertyDomainEvent<String> {
        public UserDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public UserDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String user;
    
    @javax.jdo.annotations.Column(allowsNull="false", length=50)
    @Property(
            domainEvent = UserDomainEvent.class,
            hidden = Where.PARENTED_TABLES
    )
    @MemberOrder(name="Identifiers", sequence = "10")
    public String getUser() {
        return user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }

    @Programmatic
    public String getUsername() {
        return getUser();
    }


    // //////////////////////////////////////
    // timestamp (property)
    // //////////////////////////////////////

    public static class TimestampDomainEvent extends PropertyDomainEvent<java.sql.Timestamp> {
        public TimestampDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public TimestampDomainEvent(final PublishedEvent source, final Identifier identifier, final Timestamp oldValue, final Timestamp newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private java.sql.Timestamp timestamp;

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(allowsNull="false")
    @Property(
            domainEvent = TimestampDomainEvent.class,
            hidden = Where.PARENTED_TABLES
    )
    @MemberOrder(name="Identifiers", sequence = "20")
    public java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    


    // //////////////////////////////////////
    // transactionId
    // //////////////////////////////////////

    public static class TransactionIdDomainEvent extends PropertyDomainEvent<UUID> {
        public TransactionIdDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public TransactionIdDomainEvent(final PublishedEvent source, final Identifier identifier, final UUID oldValue, final UUID newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private UUID transactionId;

    /**
     * The unique identifier (a GUID) of the transaction in which this published event was persisted.
     * 
     * <p>
     * The combination of ({@link #getTransactionId() transactionId}, {@link #getSequence() sequence}) makes up the
     * primary key.
     */
    @javax.jdo.annotations.PrimaryKey
    @javax.jdo.annotations.Column(allowsNull="false",length=JdoColumnLength.TRANSACTION_ID)
    @Property(
            domainEvent = TransactionIdDomainEvent.class,
            hidden = Where.PARENTED_TABLES
    )
    @MemberOrder(name="Identifiers", sequence = "30")
    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(final UUID transactionId) {
        this.transactionId = transactionId;
    }

    
    // //////////////////////////////////////
    // sequence
    // //////////////////////////////////////

    public static class SequenceDomainEvent extends PropertyDomainEvent<Integer> {
        public SequenceDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public SequenceDomainEvent(final PublishedEvent source, final Identifier identifier, final Integer oldValue, final Integer newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private int sequence;

    /**
     * The 0-based additional identifier of a published event within the given {@link #getTransactionId() transaction}.
     * 
     * <p>
     * The combination of ({@link #getTransactionId() transactionId}, {@link #getSequence() sequence}) makes up the
     * primary key.
     */
    @javax.jdo.annotations.PrimaryKey
    @Property(
            domainEvent = SequenceDomainEvent.class
    )
    @MemberOrder(name="Identifiers", sequence = "40")
    public int getSequence() {
        return sequence;
    }

    public void setSequence(final int sequence) {
        this.sequence = sequence;
    }
    

    // //////////////////////////////////////
    // title
    // //////////////////////////////////////

    public static class TitleDomainEvent extends PropertyDomainEvent<String> {
        public TitleDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public TitleDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String title;

    /**
     * Consists of the full oidStr (with version info etc), concatenated 
     * (if an {@link EventType#ACTION_INVOCATION}) with the name/parms of the action.
     * 
     * <p>
     * @deprecated - the oid of the target is also available (without the version info) through {@link #getTarget()}, and
     *               the action identifier is available through {@link #getMemberIdentifier()}.
     */
    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.PublishedEvent.TITLE)
    @Property(
            domainEvent = TitleDomainEvent.class,
            hidden = Where.EVERYWHERE
    )
    @Deprecated
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
    
    
    // //////////////////////////////////////
    // eventType (property)
    // //////////////////////////////////////

    public static class EventTypeDomainEvent extends PropertyDomainEvent<EventType> {
        public EventTypeDomainEvent(final PublishedEvent source, final Identifier identifier, final EventType oldValue, final EventType newValue) {
            super(source, identifier, oldValue, newValue);
        }

        public EventTypeDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }
    }

    private EventType eventType;

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.PublishedEvent.EVENT_TYPE)
    @Property(
            domainEvent = EventTypeDomainEvent.class
    )
    @MemberOrder(name="Identifiers",sequence = "50")
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(final EventType eventType) {
        this.eventType = eventType;
    }
    

    // //////////////////////////////////////
    // targetClass (property)
    // //////////////////////////////////////

    public static class TargetClassDomainEvent extends PropertyDomainEvent<String> {
        public TargetClassDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public TargetClassDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }


    private String targetClass;

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.TARGET_CLASS)
    @Property(
            domainEvent = TargetClassDomainEvent.class
    )
    @PropertyLayout(
            named = "Class",
            typicalLength = 30
    )
    @MemberOrder(name="Target", sequence = "10")
    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(final String targetClass) {
        this.targetClass = Util.abbreviated(targetClass, JdoColumnLength.TARGET_CLASS);
    }


    // //////////////////////////////////////
    // targetAction (property)
    // //////////////////////////////////////

    public static class TargetActionDomainEvent extends PropertyDomainEvent<String> {
        public TargetActionDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public TargetActionDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String targetAction;
    
    /**
     * Only populated for {@link EventType#ACTION_INVOCATION}
     */
    @javax.jdo.annotations.Column(allowsNull="true", length=JdoColumnLength.TARGET_ACTION)
    @Property(
            domainEvent = TargetActionDomainEvent.class
    )
    @PropertyLayout(
            named = "Action",
            typicalLength = 30
    )
    @MemberOrder(name="Target", sequence = "20")
    public String getTargetAction() {
        return targetAction;
    }
    
    public void setTargetAction(final String targetAction) {
        this.targetAction = Util.abbreviated(targetAction, JdoColumnLength.TARGET_ACTION);
    }
    

    // //////////////////////////////////////
    // target (property)
    // openTargetObject (action)
    // //////////////////////////////////////

    public static class TargetStrDomainEvent extends PropertyDomainEvent<String> {
        public TargetStrDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public TargetStrDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String targetStr;
    @javax.jdo.annotations.Column(allowsNull="true", length=JdoColumnLength.BOOKMARK, name="target")
    @Property(
            domainEvent = TargetStrDomainEvent.class
    )
    @PropertyLayout(
            named = "Object"
    )
    @MemberOrder(name="Target", sequence="30")
    public String getTargetStr() {
        return targetStr;
    }

    public void setTargetStr(final String targetStr) {
        this.targetStr = targetStr;
    }


    // //////////////////////////////////////
    // memberIdentifier (property)
    // //////////////////////////////////////

    public static class MemberIdentifierDomainEvent extends PropertyDomainEvent<String> {
        public MemberIdentifierDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public MemberIdentifierDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String memberIdentifier;
    
    /**
     * Holds a string representation of the invoked action, equivalent to
     * {@link Identifier#toClassAndNameIdentityString()}.
     * 
     * <p>
     * Only populated for {@link EventType#ACTION_INVOCATION}, 
     * returns <tt>null</tt> otherwise.
     * 
     * <p>
     * This property is called 'memberIdentifier' rather than 'actionIdentifier' for
     * consistency with other services (such as auditing and publishing) that may act on
     * properties rather than simply just actions.
     */
    @javax.jdo.annotations.Column(allowsNull="true", length=JdoColumnLength.MEMBER_IDENTIFIER)
    @Property(
            domainEvent = MemberIdentifierDomainEvent.class
    )
    @PropertyLayout(
            hidden = Where.ALL_TABLES,
            typicalLength = 60
    )
    @MemberOrder(name="Detail",sequence = "20")
    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(final String actionIdentifier) {
        this.memberIdentifier = Util.abbreviated(actionIdentifier, JdoColumnLength.MEMBER_IDENTIFIER);
    }



    // //////////////////////////////////////
    // state (property)
    // //////////////////////////////////////

    public static class StateDomainEvent extends PropertyDomainEvent<State> {
        public StateDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public StateDomainEvent(final PublishedEvent source, final Identifier identifier, final State oldValue, final State newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static enum State {
        QUEUED, PROCESSED
    }

    private State state;

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.PublishedEvent.STATE)
    @Property(
            domainEvent = StateDomainEvent.class
    )
    @MemberOrder(name="State", sequence = "30")
    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }
    private PublishedEvent setStateAndReturn(State state) {
        setState(state);
        return this;
    }
    

    // //////////////////////////////////////
    // serializedFormZipped (property)
    // serializedForm (derived property)
    // //////////////////////////////////////

    public static class SerializedFormDomainEvent extends PropertyDomainEvent<String> {
        public SerializedFormDomainEvent(final PublishedEvent source, final Identifier identifier) {
            super(source, identifier);
        }

        public SerializedFormDomainEvent(final PublishedEvent source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    @javax.jdo.annotations.NotPersistent
    @Property(
            domainEvent = SerializedFormDomainEvent.class,
            notPersisted = true
    )
    @PropertyLayout(
            hidden = Where.ALL_TABLES,
            multiLine = 14
    )
    @MemberOrder(name="Detail", sequence = "40")
    public String getSerializedForm() {
        byte[] zipped = getSerializedFormZipped();
        if(zipped != null) {
            return PublishingService.fromZippedBytes(zipped);
        } else {
            return getSerializedFormClob();
        }
    }


    // //////////////////////////////////////

    @Deprecated
    @javax.jdo.annotations.Column(allowsNull="true")
    private byte[] serializedFormZipped;

    @Deprecated
    @Programmatic // ignored by Isis
    public byte[] getSerializedFormZipped() {
        return serializedFormZipped;
    }

    @Deprecated
    public void setSerializedFormZipped(final byte[] serializedFormZipped) {
        this.serializedFormZipped = serializedFormZipped;
    }

    // //////////////////////////////////////

    private String serializedFormClob;

    @Programmatic // ignored by Isis
    @javax.jdo.annotations.Column(allowsNull="true", jdbcType="CLOB", sqlType="LONGVARCHAR")
    public String getSerializedFormClob() {
        return serializedFormClob;
    }

    public void setSerializedFormClob(final String serializedFormClob) {
        this.serializedFormClob = serializedFormClob;
    }


    // //////////////////////////////////////
    // processed (action)
    // //////////////////////////////////////

    public static class ProcessedDomainEvent extends ActionDomainEvent {
        public ProcessedDomainEvent(final PublishedEvent source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }


    @Action(
            domainEvent = ProcessedDomainEvent.class,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder( name="State", sequence="10")
    public PublishedEvent processed() {
        return setStateAndReturn(State.PROCESSED);
    }


    // //////////////////////////////////////
    // reQueue   (action)
    // //////////////////////////////////////

    public static class ReQueueDomainEvent extends ActionDomainEvent {
        public ReQueueDomainEvent(final PublishedEvent source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }

    @Action(
            domainEvent = ReQueueDomainEvent.class,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(name="State", sequence="11")
    public PublishedEvent reQueue() {
        return setStateAndReturn(State.QUEUED);
    }

    // //////////////////////////////////////
    // delete    (action)
    // //////////////////////////////////////

    public static class DeleteDomainEvent extends ActionDomainEvent {
        public DeleteDomainEvent(final PublishedEvent source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }

    @Action(
            domainEvent = DeleteDomainEvent.class,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(name="State", sequence="12")
    public void delete() {
        container.removeIfNotAlready(this);
    }
    

    // //////////////////////////////////////
    // toString
    // //////////////////////////////////////

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "targetStr,timestamp,user,eventType,memberIdentifier,state");
    }


    // //////////////////////////////////////
    // dependencies
    // //////////////////////////////////////

    @javax.inject.Inject
    private BookmarkService bookmarkService;

    @javax.inject.Inject
    private DomainObjectContainer container;

}
