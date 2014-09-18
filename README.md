# isis-module-xxx #

[![Build Status](https://travis-ci.org/isisaddons/isis-module-xxx.png?branch=master)](https://travis-ci.org/isisaddons/isis-module-xxx)

This module, intended for use with [Apache Isis](http://isis.apache.org), provides an implementation of Isis'
`PublishingService` API that persists published events using Isis' own (JDO) objectstore.  The intention is that these
are polled through some other process (for example using [Apache Camel](http://camel.apache.org)) so that external
systems can be updated.  To support this the persisted events have a simple state (QUEUED or PROCESSED) so that the
updating process can keep track of which events are outstanding.

The module also contains an implementation of the related `EventSerializer` API.  This is responsible for converting
the published events into a string format for persistence; specifically JSON.  The JSON representation used is that
of the [Restful Objects](http://restfulobjects.org) specification and includes the URL of the original publishing
object such that it can be accessed using Isis' Restful Objects viewer if required.

## Screenshots ##

The following screenshots show an example app's usage of the module.

#### Installing the Fixture Data ####

Installing fixture data...

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/010-install-fixtures.png)

... returns a simplified customer object, with a name, an address, a collection of orders and also a list of 
published events as a contributed collection.  The fixture setup results in one published event already:
 
![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/020-update-address-using-customer-action.png)

#### Updating the Customer's Address (published action) ####

The customer's `updateAddress` action is a _published_ action:
 
    @PublishedAction(value = PublishedCustomer.UpdateAddressEventPayloadFactory.class)
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

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/030-update-address-prompt.png)

When the action is invoked, a `PublishedEvent` is created by the framework, and persisted by the module:

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/040-action-invocation-event-created.png)

The `PublishedEvent` holds the following details:

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/050-action-invocation-details.png)

The `serialized form` field holds a JSON representation of the `PublishedCustomer.UpdateAddressEventPayloadFactory`
class specified in the `@PublishedAction` annotation.

In addition, the `ReferencedAddress` entity is also a published object:

    @PublishedObject // using the default payload factory
    public class ReferencedAddress ... { ... }

This means that as well as raising and persisting the action invocation event, a separate event is raised and persisted
 for the change to the `ReferencedAddress`:
 
![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/060-address-object-changed-event.png)

... the JSON representation of which includes a URL back to the changed address object: 

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/070-change-customer-object.png)

Note that both the published events (for the action invocation on customer and the change of address) are associated by
the same transaction Id (a GUID).  This GUID can also be used to associate the event back to any persisted commands (as
per the [Isis Addons Command](http://github.com/isisaddons/isis-module-command) module and to any audit entries (as per
 the [Isis Addons Audit](http://github.com/isisaddons/isis-module-audit) module).

#### Updating the Customer's Name (published changed object) ####

Changes to the customer are also published:

    @PublishedObject(value = PublishedCustomer.ObjectChangedEventPayloadFactory.class)
    public class PublishedCustomer ... { ... }

Changing the customer's name causes an event to persisted:

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/080-customer-changed-object-event-created.png)

In this case a custom payload factory is specified:

    public static class ObjectChangedEventPayloadFactory implements PublishedObject.PayloadFactory {
        public static class PublishedCustomerPayload extends EventPayloadForObjectChanged<PublishedCustomer> {

            public PublishedCustomerPayload(PublishedCustomer changed) { super(changed); }

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

This custom payload exposes additional related information: the customer's name (a simple scalar), the customer's 
address' town (traversing a reference), and the orders of the customers (traversing a collection).  This additional
information is captured in the serialized form of the event:

![](https://raw.github.com/isisaddons/isis-module-publishing/master/images/090-customer-changed-event-details.png)


## Relationship to Apache Isis Core ##

Isis Core 1.6.0 included the `org.apache.isis.core:isis-module-publishing:1.6.0` and also 
`org.apache.isis.core:isis-module-publishingeventserializer:1.6.0` Maven artifacts.  This module is a direct copy of 
the code of those two modules, with the following changes:

* package names have been altered from `org.apache.isis` to `org.isisaddons.module.publishing`
* the `persistent-unit` (in the JDO manifest) has changed from `isis-module-publishing` to 
  `org-isisaddons-module-publishing-dom`

Otherwise the functionality is identical; warts and all!

At the time of writing the plan is to remove this module from Isis Core (so it won't be in Isis 1.7.0), and instead 
continue to develop it solely as one of the [Isis Addons](http://www.isisaddons.org) modules.


## How to configure/use ##

.... UP TO HERE....


You can either use this module "out-of-the-box", or you can fork this repo and extend to your own requirements. 

To use "out-of-the-box":

* update your classpath by adding this dependency in your dom project's `pom.xml`:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.isisaddons.module.xxx&lt;/groupId&gt;
        &lt;artifactId&gt;isis-module-xxx-dom&lt;/artifactId&gt;
        &lt;version&gt;1.6.0&lt;/version&gt;
    &lt;/dependency&gt;
</pre>

* update your `WEB-INF/isis.properties`:

<pre>
    isis.services-installer=configuration-and-annotation
    isis.services.ServicesInstallerFromAnnotation.packagePrefix=
                    ...,\
                    org.isisaddons.module.xxx.xxx,\
                    ...

    isis.services = ...,\
                    org.isisaddons.module.audit.XxxContributions,\
                    ...
                    
The `XxxContributions` service is optional but recommended; see below for more information.

If instead you want to extend this module's functionality, then we recommend that you fork this repo.  The repo is 
structured as follows:

* `pom.xml   ` - parent pom
* `dom       ` - the module implementation, depends on Isis applib
* `fixture   ` - fixtures, holding a sample domain objects and fixture scripts; depends on `dom`
* `integtests` - integration tests for the module; depends on `fixture`
* `webapp    ` - demo webapp (see above screenshots); depends on `dom` and `fixture`

Xxx

## API ##

### XxxService ###

The `XxxService` defines the following API:

<pre>
public interface XxxService {
}
</pre>


## Implementation ##

## Supporting Services ##

## Related Modules/Services ##

... referenced by the [Isis Add-ons](http://www.isisaddons.org) website.


talk about TransactionId


## Legal Stuff ##
 
#### License ####

    Copyright 2014 Dan Haywood

    Licensed under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.


#### Dependencies ####

There are no third-party dependencies.

##  Maven deploy notes ##

Only the `dom` module is deployed, and is done so using Sonatype's OSS support (see 
[user guide](http://central.sonatype.org/pages/apache-maven.html)).

#### Release to Sonatype's Snapshot Repo ####

To deploy a snapshot, use:

    pushd dom
    mvn clean deploy
    popd

The artifacts should be available in Sonatype's 
[Snapshot Repo](https://oss.sonatype.org/content/repositories/snapshots).

#### Release to Maven Central (scripted process) ####

The `release.sh` script automates the release process.  It performs the following:

* perform sanity check (`mvn clean install -o`) that everything builds ok
* bump the `pom.xml` to a specified release version, and tag
* perform a double check (`mvn clean install -o`) that everything still builds ok
* release the code using `mvn clean deploy`
* bump the `pom.xml` to a specified release version

For example:

    sh release.sh 1.6.0 \
                  1.6.1-SNAPSHOT \
                  dan@haywood-associates.co.uk \
                  "this is not really my passphrase"
    
where
* `$1` is the release version
* `$2` is the snapshot version
* `$3` is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* `$4` is the corresponding passphrase for that secret key.

If the script completes successfully, then push changes:

    git push
    
If the script fails to complete, then identify the cause, perform a `git reset --hard` to start over and fix the issue
before trying again.

#### Release to Maven Central (manual process) ####

If you don't want to use `release.sh`, then the steps can be performed manually.

To start, call `bumpver.sh` to bump up to the release version, eg:

     `sh bumpver.sh 1.6.0`

which:
* edit the parent `pom.xml`, to change `${isis-module-command.version}` to version
* edit the `dom` module's pom.xml version
* commit the changes
* if a SNAPSHOT, then tag

Next, do a quick sanity check:

    mvn clean install -o
    
All being well, then release from the `dom` module:

    pushd dom
    mvn clean deploy -P release \
        -Dpgp.secretkey=keyring:id=dan@haywood-associates.co.uk \
        -Dpgp.passphrase="literal:this is not really my passphrase"
    popd

where (for example):
* "dan@haywood-associates.co.uk" is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* the pass phrase is as specified as a literal

Other ways of specifying the key and passphrase are available, see the `pgp-maven-plugin`'s 
[documentation](http://kohsuke.org/pgp-maven-plugin/secretkey.html)).

If (in the `dom`'s `pom.xml`) the `nexus-staging-maven-plugin` has the `autoReleaseAfterClose` setting set to `true`,
then the above command will automatically stage, close and the release the repo.  Sync'ing to Maven Central should 
happen automatically.  According to Sonatype's guide, it takes about 10 minutes to sync, but up to 2 hours to update 
[search](http://search.maven.org).

If instead the `autoReleaseAfterClose` setting is set to `false`, then the repo will require manually closing and 
releasing either by logging onto the [Sonatype's OSS staging repo](https://oss.sonatype.org) or alternatively by 
releasing from the command line using `mvn nexus-staging:release`.

Finally, don't forget to update the release to next snapshot, eg:

    sh bumpver.sh 1.6.1-SNAPSHOT

and then push changes.
