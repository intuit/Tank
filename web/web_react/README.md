# Tank React UI

The React front end for the JSF → React migration. Only the typed API client is here so far.

## API client

`src/api/schema.d.ts` holds TypeScript types generated from the controller's OpenAPI spec, and
`src/api/client.ts` wraps them in an [openapi-fetch](https://openapi-ts.dev/openapi-fetch/) client
that sends the session cookie and the `X-XSRF-TOKEN` header Tank requires on state-changing requests.

```ts
const api = createTankClient({ onUnauthorized: () => navigate('/login') });
const { data, error } = await api.GET('/v2/projects/{projectId}/full', {
  params: { path: { projectId: 12 } },
});
```

### Regenerating after a REST change

The Java DTOs and controller annotations are the source of truth. After changing them:

```bash
# 1. Export the spec (boots only the controllers, no database needed)
mvn -pl rest-mvc/impl test -Dtest=OpenApiSpecExportTest -Dsurefire.failIfNoSpecifiedTests=false

# 2. Regenerate the types and commit schema.d.ts with the Java change
cd web/web_react
npm run generate:api
npm run typecheck
```

`npm run check:api` regenerates and fails if `schema.d.ts` is out of date, for use in CI.

To generate from a running controller instead:
`npx openapi-typescript https://<host>/tank/v3/api-docs -o src/api/schema.d.ts`.

An endpoint whose 2xx `@ApiResponse` has `content = @Content`, or that returns a raw `Object`,
comes out with no body type. Let springdoc infer the type from the return type, or add
`@Schema(implementation = ...)`.
