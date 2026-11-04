# Tank React UI

The React front end for the JSF → React migration: React 19, React Router, PrimeReact 10 (MIT; the
lara-light-blue theme) and Vite. Pages not yet moved to React link to the JSF UI, which shares the
same session.

## How it ships

This module builds a jar (`web-react`) with the app in `META-INF/resources/app`. `tank.war` includes
it, and `CustomWebMvcConfigurer` serves it under `/app`:

- `/app/assets/**` are content-hashed and cached for a year
- any other `/app/...` path without a file extension returns `index.html`, so deep links load the app
- `index.html`'s `<base href="/app/">` is rewritten to the WAR's context path (`/tank/app/`), so one
  build works under any context path

`mvn install` downloads Node into `./node` (frontend-maven-plugin), then runs `npm ci`,
`npm run build` (type-check + Vite) and `npm test`. `-DskipTests` skips the npm tests;
`-Dskip.npm` skips npm entirely, which leaves the jar empty.

## Development

```bash
cd web/web_react
npm install
TANK_URL=http://localhost:8080/tank npm run dev   # http://localhost:5173/app/
npm test
```

The dev server proxies `/v2` to `TANK_URL` and rewrites the session cookie's path, so sign in on the
dev server's own login page.

## API client

`src/api/schema.d.ts` holds TypeScript types generated from the controller's OpenAPI spec, and
`src/api/client.ts` wraps them in an [openapi-fetch](https://openapi-ts.dev/openapi-fetch/) client
that sends the session cookie and the `X-XSRF-TOKEN` header Tank requires on state-changing requests.

```ts
const { client } = useSession();
const { data, error } = await client.GET('/v2/projects/{projectId}/full', {
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
