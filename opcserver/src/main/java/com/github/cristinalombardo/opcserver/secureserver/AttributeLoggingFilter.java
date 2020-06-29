/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.github.cristinalombardo.opcserver.secureserver;

import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilter;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilterContext.GetAttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilterContext.SetAttributeContext;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeLoggingFilter implements AttributeFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Predicate<AttributeId> attributePredicate;

    public AttributeLoggingFilter() {
        this(attributeId -> true);
    }

    public AttributeLoggingFilter(Predicate<AttributeId> attributePredicate) {
        this.attributePredicate = attributePredicate;
    }

    @Override
    public Object getAttribute(GetAttributeContext ctx, AttributeId attributeId) {
        Object value = ctx.getAttribute(attributeId);

        // only log external reads
        if (attributePredicate.test(attributeId) && ctx.getSession().isPresent()) {
            logger.debug(
                "get nodeId={} attributeId={} value={}",
                ctx.getNode().getNodeId(), attributeId, value
            );
        }

        return value;
    }

    @Override
    public void setAttribute(SetAttributeContext ctx, AttributeId attributeId, Object value) {
        // only log external writes
        if (attributePredicate.test(attributeId) && ctx.getSession().isPresent()) {
            logger.info(
                "set nodeId={} attributeId={} value={}",
                ctx.getNode().getNodeId(), attributeId, value
            );
        }

        ctx.setAttribute(attributeId, value);
    }

}
