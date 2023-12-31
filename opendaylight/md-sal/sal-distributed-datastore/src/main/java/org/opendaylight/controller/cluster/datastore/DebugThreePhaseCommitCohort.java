/*
 * Copyright (c) 2015 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.datastore;

import static java.util.Objects.requireNonNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.opendaylight.controller.cluster.access.concepts.TransactionIdentifier;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.yangtools.yang.common.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Future;

/**
 * An AbstractThreePhaseCommitCohort implementation used for debugging. If a failure occurs, the transaction
 * call site is printed.
 *
 * @author Thomas Pantelis
 */
class DebugThreePhaseCommitCohort extends AbstractThreePhaseCommitCohort<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(DebugThreePhaseCommitCohort.class);

    private final AbstractThreePhaseCommitCohort<?> delegate;
    private final Throwable debugContext;
    private final TransactionIdentifier transactionId;

    @SuppressFBWarnings("SLF4J_LOGGER_SHOULD_BE_FINAL")
    private Logger log = LOG;

    DebugThreePhaseCommitCohort(final TransactionIdentifier transactionId,
            final AbstractThreePhaseCommitCohort<?> delegate, final Throwable debugContext) {
        this.delegate = requireNonNull(delegate);
        this.debugContext = requireNonNull(debugContext);
        this.transactionId = requireNonNull(transactionId);
    }

    private <V> ListenableFuture<V> addFutureCallback(final ListenableFuture<V> future) {
        Futures.addCallback(future, new FutureCallback<V>() {
            @Override
            public void onSuccess(final V result) {
                // no-op
            }

            @Override
            public void onFailure(final Throwable failure) {
                log.warn("Transaction {} failed with error \"{}\" - was allocated in the following context",
                        transactionId, failure, debugContext);
            }
        }, MoreExecutors.directExecutor());

        return future;
    }

    @Override
    public ListenableFuture<Boolean> canCommit() {
        return addFutureCallback(delegate.canCommit());
    }

    @Override
    public ListenableFuture<Empty> preCommit() {
        return addFutureCallback(delegate.preCommit());
    }

    @Override
    public ListenableFuture<? extends CommitInfo> commit() {
        return addFutureCallback(delegate.commit());
    }

    @Override
    public ListenableFuture<Empty> abort() {
        return delegate.abort();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    List<Future<Object>> getCohortFutures() {
        return ((AbstractThreePhaseCommitCohort)delegate).getCohortFutures();
    }

    @VisibleForTesting
    void setLogger(final Logger logger) {
        log = logger;
    }
}
