/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.access.commands;

/**
 * Externalizable proxy for use with {@link TransactionPreCommitRequest}. It implements the initial (Boron)
 * serialization format.
 */
@Deprecated(since = "7.0.0", forRemoval = true)
final class TransactionPreCommitRequestProxyV1 extends AbstractTransactionRequestProxy<TransactionPreCommitRequest>
        implements TransactionPreCommitRequest.SerialForm {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    // checkstyle flags the public modifier as redundant however it is explicitly needed for Java serialization to
    // be able to create instances via reflection.
    @SuppressWarnings("checkstyle:RedundantModifier")
    public TransactionPreCommitRequestProxyV1() {
        // For Externalizable
    }

    TransactionPreCommitRequestProxyV1(final TransactionPreCommitRequest request) {
        super(request);
    }
}
