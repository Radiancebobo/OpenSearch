/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.cluster.routing.allocation.decider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.cluster.node.DiscoveryNode;
import org.opensearch.cluster.routing.RoutingNode;
import org.opensearch.cluster.routing.ShardRouting;
import org.opensearch.cluster.routing.allocation.RoutingAllocation;
import org.opensearch.common.annotation.PublicApi;

import java.util.Collection;
import java.util.Collections;

/**
 * A composite {@link AllocationDecider} combining the "decision" of multiple
 * {@link AllocationDecider} implementations into a single allocation decision.
 *
 * @opensearch.api
 */
@PublicApi(since = "1.0.0")
public class AllocationDeciders extends AllocationDecider {

    private static final Logger logger = LogManager.getLogger(AllocationDeciders.class);

    private final Collection<AllocationDecider> allocations;

    public AllocationDeciders(Collection<AllocationDecider> allocations) {
        this.allocations = Collections.unmodifiableCollection(allocations);
    }

    @Override
    public Decision canRebalance(ShardRouting shardRouting, RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.canRebalance(shardRouting, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canAllocate(ShardRouting shardRouting, RoutingNode node, RoutingAllocation allocation) {
        if (allocation.shouldIgnoreShardForNode(shardRouting.shardId(), node.nodeId())) {
            return Decision.NO;
        }
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.canAllocate(shardRouting, node, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                        "Can not allocate [{}] on node [{}] due to [{}]",
                        shardRouting,
                        node.node(),
                        allocationDecider.getClass().getSimpleName()
                    );
                }
                // short circuit only if debugging is not enabled
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canRemain(ShardRouting shardRouting, RoutingNode node, RoutingAllocation allocation) {
        if (allocation.shouldIgnoreShardForNode(shardRouting.shardId(), node.nodeId())) {
            if (logger.isTraceEnabled()) {
                logger.trace("Shard [{}] should be ignored for node [{}]", shardRouting, node.nodeId());
            }
            return Decision.NO;
        }
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.canRemain(shardRouting, node, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                        "Shard [{}] can not remain on node [{}] due to [{}]",
                        shardRouting,
                        node.nodeId(),
                        allocationDecider.getClass().getSimpleName()
                    );
                }
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canAllocate(IndexMetadata indexMetadata, RoutingNode node, RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.canAllocate(indexMetadata, node, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision shouldAutoExpandToNode(IndexMetadata indexMetadata, DiscoveryNode node, RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.shouldAutoExpandToNode(indexMetadata, node, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canAllocate(ShardRouting shardRouting, RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.canAllocate(shardRouting, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canRebalance(RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider allocationDecider : allocations) {
            Decision decision = allocationDecider.canRebalance(allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canForceAllocatePrimary(ShardRouting shardRouting, RoutingNode node, RoutingAllocation allocation) {
        assert shardRouting.primary() : "must not call canForceAllocatePrimary on a non-primary shard routing " + shardRouting;

        if (allocation.shouldIgnoreShardForNode(shardRouting.shardId(), node.nodeId())) {
            return Decision.NO;
        }
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider decider : allocations) {
            Decision decision = decider.canForceAllocatePrimary(shardRouting, node, allocation);
            // short track if a NO is returned.
            if (decision == Decision.NO) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                        "Shard [{}] can not be forcefully allocated to node [{}] due to [{}].",
                        shardRouting.shardId(),
                        node.nodeId(),
                        decider.getClass().getSimpleName()
                    );
                }
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canAllocateAnyShardToNode(RoutingNode node, RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider decider : allocations) {
            Decision decision = decider.canAllocateAnyShardToNode(node, allocation);
            if (decision.type().canPremptivelyReturn()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Shard can not be allocated on node [{}] due to [{}]", node.nodeId(), decider.getClass().getSimpleName());
                }
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canMoveAway(ShardRouting shardRouting, RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider decider : allocations) {
            Decision decision = decider.canMoveAway(shardRouting, allocation);
            // short track if a NO is returned.
            if (decision.type().canPremptivelyReturn()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Shard [{}] can not be moved away due to [{}]", shardRouting, decider.getClass().getSimpleName());
                }
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    @Override
    public Decision canMoveAnyShard(RoutingAllocation allocation) {
        Decision.Multi ret = new Decision.Multi();
        for (AllocationDecider decider : allocations) {
            Decision decision = decider.canMoveAnyShard(allocation);
            // short track if a NO is returned.
            if (decision.type().canPremptivelyReturn()) {
                if (allocation.debugDecision() == false) {
                    return decision;
                } else {
                    ret.add(decision);
                }
            } else {
                addDecision(ret, decision, allocation);
            }
        }
        return ret;
    }

    private void addDecision(Decision.Multi ret, Decision decision, RoutingAllocation allocation) {
        // We never add ALWAYS decisions and only add YES decisions when requested by debug mode (since Multi default is YES).
        if (decision != Decision.ALWAYS
            && (allocation.getDebugMode() == RoutingAllocation.DebugMode.ON || decision.type() != Decision.Type.YES)) {
            ret.add(decision);
        }
    }
}
