/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.api.resource.observation;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Listener for resource change events. Event listeners are notified asynchronously, and see events after they occur and the transaction is committed
 * (which means the changes may already be visible to {@link ResourceResolver} objects before they are being processed by listeners).
 *
 * <p>
 * {@code ResourceChangeListener} objects are registered with the Framework service
 * registry and are notified with {@code ResourceChange} objects when a
 * change occurs.
 * <p>
 * {@code ResourceChangeListener} objects can inspect the received {@code ResourceChange} objects to
 * determine the type of change, location and other properties.
 *
 * <p>
 * {@code ResourceChangeListener} objects must be registered with a service property
 * {@link ResourceChangeListener#PATHS} whose value is the list of resource paths for which
 * the listener is interested in getting change events.
 *
 * <p>
 * By default a resource listener gets only local events which means events
 * caused by changes persisted on the same instance as this listener is registered.
 * If the resource listener is interested in external events, the implementation
 * should implement the {@link ExternalResourceChangeListener} interface, but still register
 * the service as a {@code ResourceChangeListener} service.
 *
 * @since 1.0.0 (Sling API Bundle 2.11.0)
 */
@ConsumerType
public interface ResourceChangeListener {

    /**
     * <p>Array of paths or glob patterns - required.</p>
     *
     * <p>A path is either absolute or relative. If it's a relative path, the relative path will be appended to all search paths of the
     * resource resolver.</p>
     *
     * <p>If the whole tree of all search paths should be observed, the special value {@code .} should be used.</p>
     *
     * <p>A glob pattern must start with the {@code glob:} prefix (e.g. <code>glob:**&#47;*.html</code>). The following rules are used
     * to interpret glob patterns:</p>
     * <ul>
     *     <li>The {@code *} character matches zero or more characters of a name component without crossing directory boundaries.</li>
     *     <li>The {@code **} characters match zero or more characters crossing directory boundaries.</li>
     *     <li>If the pattern is relative (does not start with a slash), the relative path will be appended to all search paths of
     *        the resource resolver.
     * </ul>
     *
     * <p>
     * In general, it can't be guaranteed that the underlying implementation of the resources will send a remove/add
     * event for each removed/added resource. For example if the root of a tree, like {@code /foo} is removed, the underlying
     * implementation might only send a single remove event for {@code /foo} but not for any child resources.
     * Similarly for renaming a subtree from {@code /foo} to {@code /bar} the add event may only be sent for {@code /bar}
     * but not for any child resources.
     * Therefore a listener might get remove/add events for resources that not directly match the specified paths/path patterns.
     * For example if a listener is registered for path {@code /foo/bar} and {@code /foo} is removed, the listener will get a remove event for {@code /foo}.
     * The same is true if any pattern is used and any parent of a matching resource is removed/added. The listener
     * must handle these events accordingly.
     *
     * <p>If one of the paths is a sub resource of another specified path, the sub path is ignored.</p>
     *
     * <p>If this property is missing or invalid, the listener is ignored. The type of the property must either be String, or a String
     * array.</p>
     */
    String PATHS = "resource.paths";

    /**
     * Array of change types - optional.
     * If this property is missing, added, removed and changed events for resources
     * and added and removed events for resource providers are reported.
     * If this property is invalid, the listener is ignored. The type of the property
     * must either be String, or a String array. Valid values are the constants from this class
     * whose names are starting with {@code CHANGE_}. They map to one of the values of {@link ResourceChange.ChangeType}.
     */
    String CHANGES = "resource.change.types";

    /**
     * String constant for {@link ResourceChange.ChangeType#ADDED}.
     * @since 1.3.0 (Sling API Bundle 2.23.0)
     */
    String CHANGE_ADDED = "ADDED";

    /**
     * String constant for {@link ResourceChange.ChangeType#REMOVED}.
     * @since 1.3.0 (Sling API Bundle 2.23.0)
     */
    String CHANGE_REMOVED = "REMOVED";

    /**
     * String constant for {@link ResourceChange.ChangeType#CHANGED}.
     * @since 1.3.0 (Sling API Bundle 2.23.0)
     */
    String CHANGE_CHANGED = "CHANGED";

    /**
     * String constant for {@link ResourceChange.ChangeType#PROVIDER_ADDED}.
     * @since 1.3.0 (Sling API Bundle 2.23.0)
     */
    String CHANGE_PROVIDER_ADDED = "PROVIDER_ADDED";

    /**
     * String constant for {@link ResourceChange.ChangeType#PROVIDER_REMOVED}.
     * @since 1.3.0 (Sling API Bundle 2.23.0)
     */
    String CHANGE_PROVIDER_REMOVED = "PROVIDER_REMOVED";

    /**
     * An optional hint indicating to the underlying implementation that for
     * changes regarding properties (added/removed/changed) the listener
     * is only interested in those property names listed inhere.
     * If the underlying implementation supports this, events for property names that
     * are not enlisted here will not be delivered, however events
     * concerning resources are not affected by this hint.
     * This is only a hint, a change listener registering with this property
     * must be prepared that the underlying implementation is not able to filter
     * based on this property. In this case the listener gets all events
     * as defined with the other properties.
     */
    String PROPERTY_NAMES_HINT = "resource.property.names.hint";

    /**
     * Report resource changes based on the filter properties of this listener.
     * <p>
     * Note that resource changes for paths which are <strong>ancestors</strong> of the paths this
     * listener was registered to may be reported through this method. This is due to limitations of
     * certain resource providers to provide events on a more granular level
     * (e.g. for deletion or movement of resources containing (potentially nested) child resources).
     * <p>
     * Starting with version 1.2 of this API, an instance of {@code ResoureChangeList} is passed
     * as the parameter to allow passing additional information.
     *
     * @param changes The changes list. This list is immutable.
     */
    void onChange(@NotNull List<ResourceChange> changes);
}
