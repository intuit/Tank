/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.docs;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.intuit.tank.rest.mvc.rest.models.DataFileSummary;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ProjectSummary;
import com.intuit.tank.rest.mvc.rest.models.ScriptSummary;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Documents the paged forms of {@code GET /v2/projects}, {@code /v2/scripts} and {@code /v2/datafiles}.
 * <p>
 * Each is a second handler on the same path, selected by the {@code page} parameter. OpenAPI allows one
 * operation per path and method, so springdoc merges the two: it keeps the unpaged response type, never
 * registers the {@code PageResponse} schema, and marks {@code page} required. This makes the 200 response
 * {@code oneOf} the two bodies and {@code page} optional, so generated clients see both forms.
 */
@Component
public class PagedListsOpenApiCustomizer implements OpenApiCustomizer {

    record PagedList(String path, Class<?> itemType) {
    }

    static final List<PagedList> PAGED_LISTS = List.of(
            new PagedList("/v2/projects", ProjectSummary.class),
            new PagedList("/v2/scripts", ScriptSummary.class),
            new PagedList("/v2/datafiles", DataFileSummary.class));

    @Override
    public void customise(OpenAPI openApi) {
        for (PagedList list : PAGED_LISTS) {
            PathItem item = openApi.getPaths() == null ? null : openApi.getPaths().get(list.path());
            Operation get = item == null ? null : item.getGet();
            if (get == null || get.getResponses() == null) {
                continue;
            }
            ApiResponse ok = get.getResponses().get("200");
            MediaType json = ok == null || ok.getContent() == null ? null
                    : ok.getContent().get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
            if (json == null || json.getSchema() == null) {
                continue;
            }
            String pageRef = registerPageSchema(openApi, list.itemType());
            Schema<?> unpaged = json.getSchema();
            json.setSchema(new Schema<>().oneOf(List.of(unpaged, new Schema<>().$ref(pageRef))));
            ok.setDescription("With page, one page of summaries; without it, every item");
            if (get.getParameters() != null) {
                for (Parameter parameter : get.getParameters()) {
                    if ("page".equals(parameter.getName())) {
                        parameter.setRequired(false);
                        parameter.setDescription("Zero-based page number. Selects the paged form of the response");
                    }
                }
            }
        }
    }

    /**
     * @return the $ref of {@code PageResponse<itemType>}, e.g. {@code #/components/schemas/PageResponseProjectSummary}
     */
    private static String registerPageSchema(OpenAPI openApi, Class<?> itemType) {
        AnnotatedType type = new AnnotatedType(
                TypeFactory.defaultInstance().constructParametricType(PageResponse.class, itemType)).resolveAsRef(true);
        ResolvedSchema resolved = ModelConverters.getInstance(true).resolveAsResolvedSchema(type);
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
