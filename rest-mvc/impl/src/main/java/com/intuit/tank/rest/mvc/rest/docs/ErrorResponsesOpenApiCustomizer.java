/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.docs;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericExceptionHandler;
import com.intuit.tank.rest.mvc.rest.controllers.errors.SimpleErrorResponseBody;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

/**
 * Documents the body of every error response, which controllers declare with an empty
 * {@code content}: the JSON {@code ErrorResponse} that {@link GenericExceptionHandler} returns for
 * service errors, or a plain string from its handlers for framework errors (a bad upload, an
 * unreadable body, an unknown path). Generated clients can then read the server's message.
 */
@Component
public class ErrorResponsesOpenApiCustomizer implements OpenApiCustomizer {

    static final String SCHEMA = "ErrorResponse";

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }
        String ref = registerErrorSchema(openApi);
        for (PathItem item : openApi.getPaths().values()) {
            for (Operation operation : item.readOperations()) {
                if (operation.getResponses() == null) {
                    continue;
                }
                operation.getResponses().forEach((code, response) -> {
                    if (isError(code) && (response.getContent() == null || response.getContent().isEmpty())) {
                        response.setContent(errorContent(ref));
                    }
                });
            }
        }
    }

    static boolean isError(String code) {
        return code.startsWith("4") || code.startsWith("5") || "default".equals(code);
    }

    private static Content errorContent(String ref) {
        return new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<>().$ref(ref)))
                .addMediaType(org.springframework.http.MediaType.TEXT_PLAIN_VALUE,
                        new MediaType().schema(new StringSchema()
                                .description("A message, from a framework error such as a bad upload or an unreadable body")));
    }

    /**
     * @return the $ref of {@code ErrorResponse}, adding the schema when no controller referenced it
     */
    private static String registerErrorSchema(OpenAPI openApi) {
        ResolvedSchema resolved = ModelConverters.getInstance(true)
                .resolveAsResolvedSchema(new AnnotatedType(SimpleErrorResponseBody.class).resolveAsRef(true));
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        resolved.referencedSchemas.forEach((name, schema) -> {
            if (openApi.getComponents().getSchemas() == null
                    || !openApi.getComponents().getSchemas().containsKey(name)) {
                openApi.getComponents().addSchemas(name, schema);
            }
        });
        return resolved.schema.get$ref();
    }
}
