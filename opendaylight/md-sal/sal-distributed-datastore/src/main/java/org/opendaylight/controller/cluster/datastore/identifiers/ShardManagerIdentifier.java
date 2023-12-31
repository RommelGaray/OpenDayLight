/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.cluster.datastore.identifiers;

public class ShardManagerIdentifier {
    private final String type;

    public ShardManagerIdentifier(final String type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return type.equals(((ShardManagerIdentifier) obj).type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override public String toString() {
        return "shardmanager-" + type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;

        public Builder type(final String newType) {
            type = newType;
            return this;
        }

        public ShardManagerIdentifier build() {
            return new ShardManagerIdentifier(type);
        }
    }
}
