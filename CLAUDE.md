# Tank Project — UI Overhaul Branch

## Branch
- **Branch**: `zkofiro/tank-ui-overhual`
- **Goal**: Modernize the Tank web UI/UX incrementally — from dated JSF to a world-class load testing tool
- **Strategy**: Strangler Fig — keep same layout, modernize CSS/UX first, then migrate page-by-page to React
- **Target stack**: React + TypeScript + Vite + PrimeReact + TanStack Query
- **Competitor reference**: Gatling Enterprise (Frontline) — clean, modern, data-dense dashboards

## Workflow Rules
- **Never commit or push** — always present commands for the user to run
- **Manual validation required** before any commit/merge/deploy
- **TDD mandatory** — strict Red-Green-Refactor for all feature work
  - Announce TDD phase before every task: "Starting RED phase for [description]"
  - No implementation code until a failing test exists
  - Phase-labeled commit messages: `[RED]`, `[GREEN]`, `[REFACTOR]`
- **Do not break existing functionality** — every change must be backwards-compatible or behind a feature flag
- **Preserve layout** — users have used this layout for 10+ years; keep structure familiar, improve look & feel

## Project Overview
Tank is a load-testing platform. This branch focuses on incrementally modernizing the web UI layer.

## Architecture Decision: Why PrimeReact

**Current:** JSF 6.0 + PrimeFaces 15.0.12 (server-rendered, extreme coupling to JSF lifecycle)

**Target:** React + PrimeReact (client-side SPA consuming REST APIs)

**Why PrimeReact over MUI/Shadcn/Ant:**
- Same component names as PrimeFaces (DataTable, TreeTable, Dialog, etc.) — lowest learning curve
- Same theme system (PrimeOne themes work across both) — visual continuity during migration
- All components free (MUI X DataGrid Pro/Premium is $15k+/yr)
- Built-in DataTable with lazy load, virtual scroll, column reorder, inline editing — matches current PrimeFaces features
- Styled or unstyled mode with Tailwind integration

**Why Strangler Fig over Big Bang:**
- JSF is EXTREMELY coupled — every button, table, dialog is a PrimeFaces component
- All state lives server-side (ConversationScoped, ViewScoped, SessionScoped beans)
- Every interaction is a server round-trip (`<p:ajax>` listeners everywhere)
- Can't embed React inside JSF pages — must coexist at route level
- Pages migrate one at a time; app stays fully functional throughout

**Coexistence model:**
```
/app/legacy/*   →  JSF/PrimeFaces (existing WAR)
/app/modern/*   →  React SPA (new, served as static files)
/v2/*           →  Shared REST backend (already 50+ endpoints)
```

## Tech Stack

### Current (JSF — being phased out)
- **Java 17**, Maven multi-module
- **Web UI**: Jakarta Faces (JSF 6.0) + PrimeFaces 15.0.12 (Jakarta), WAR deployment
- **No frontend build tooling** (no webpack/npm/node) — purely server-side JSF with static CSS/JS
- **Charts**: ChartJS via `chartjs-java-model`
- **JS libs**: jQuery (via PrimeFaces), CodeMirror, jstz

### Target (React SPA — being built)
- **React 18+** with TypeScript
- **Vite** for build tooling
- **PrimeReact** for UI components (DataTable, Dialog, TreeTable, etc.)
- **TanStack Query** for data fetching/caching
- **React Router** for client-side routing
- **PrimeOne theme** (customized) for visual consistency

### Shared (unchanged)
- **REST API**: Spring Boot (Spring MVC), v2 endpoints at `/v2/*`, Swagger/OpenAPI 3.x
- **Auth**: Bearer token + session-based (RestSecurityFilter)
- **Testing**: JUnit 5, Mockito 5.20
- **Logging**: Log4j 2 — `private static final Logger LOG = LogManager.getLogger(ClassName.class);`
- **XML binding**: JAXB
- **Base package**: `com.intuit.tank`

## Key Directories
| Area | Path |
|------|------|
| Web UI module (JSF) | `web/web_ui/` |
| JSF backend beans | `web/web_support/` |
| XHTML templates | `web/web_ui/src/main/webapp/` |
| Master template | `web/web_ui/src/main/webapp/WEB-INF/templates/tank.xhtml` |
| CSS/JS resources | `web/web_ui/src/main/webapp/resources/` |
| Custom CSS | `web/web_ui/src/main/webapp/resources/css/tank.css` |
| PrimeFaces overrides | `web/web_ui/src/main/webapp/resources/css/primefaces-override.css` |
| Base screen CSS | `web/web_ui/src/main/webapp/resources/css/screen.css` |
| Custom JS | `web/web_ui/src/main/webapp/resources/js/tank.js` |
| REST controllers | `rest-mvc/impl/src/main/java/.../rest/controllers/` |
| REST services | `rest-mvc/impl/src/main/java/.../rest/services/` |
| API DTOs/models | `api/src/main/java/com/intuit/tank/` |
| Debugger UI (Swing) | `tools/agent_debugger/` |

## Current UI Coupling Assessment

| Layer | Coupling to JSF | Migration Difficulty |
|-------|----------------|---------------------|
| **XHTML templates** (60+ pages) | Extreme — Facelets + PF components | Full rewrite per page |
| **Managed beans** (ViewScoped, ConversationScoped) | Extreme — server holds ALL state | Extract logic to REST services |
| **AJAX interactions** (`p:ajax`, `PF()` widget API) | Extreme — every click is a server round-trip | Replace with REST calls + React state |
| **Real-time updates** (`p:poll interval="300"`) | High — polling, no WebSocket | Replace with WebSocket/SSE |
| **CSS** (~700 lines, 3 files) | Loose — can replace independently | Easy — first thing to modernize |
| **Custom JS** (~260 lines, `tank.js`) | Loose — jQuery utils, scroll hacks | Easy — replace with React patterns |

## Existing REST API Coverage
**8 controllers, ~50+ endpoints** — good CRUD coverage for:
- Projects, Jobs (incl. start/stop/pause/resume/kill), Scripts, DataFiles, Filters, Agent, Logs

**Major gaps (JSF-only, no REST — must fill before SPA migration):**
- User management (CRUD, permissions, account settings)
- Reporting/analytics (TPS, metrics, real-time monitoring, historical data)
- Script editor operations (step CRUD, validation, debugging)
- Advanced filter builder
- Live monitoring (needs WebSocket/SSE)
- Dashboard/analytics aggregation

---

## Modernization Roadmap

### Phase 1: Visual Refresh (CSS-only, keep JSF, same layout)
> Make it look modern without touching page structure, functionality, or JSF code.
> Users see the same layout they've known for 10+ years, but everything looks polished.

- [ ] **1.1 — Color system & CSS custom properties**: Define a modern palette as CSS variables (replace all hardcoded hex). Dark-friendly from day one.
- [ ] **1.2 — Typography & spacing**: Modern font stack (Inter/system-ui), consistent spacing scale, improved readability
- [ ] **1.3 — PrimeFaces theme override**: Flat design, subtle shadows, rounded corners, modern hover/focus states in `primefaces-override.css`
- [ ] **1.4 — Navigation bar**: Cleaner header, better active states, modern user menu — CSS + minimal XHTML changes to master template
- [ ] **1.5 — Data table polish**: Modern row styling, better pagination look, improved filter/sort visuals
- [ ] **1.6 — Form & input modernization**: Input styling, labels, validation feedback, button hierarchy
- [ ] **1.7 — Dialog/modal refresh**: Modern overlay backdrop, better sizing, clean close/confirm buttons
- [ ] **1.8 — Loading & feedback states**: Better ajax spinner, modern toast notifications
- [ ] **1.9 — Layout foundation**: Replace float-based layout with Flexbox in `screen.css`; responsive at 1280/1440/1920

### Phase 2: UX Improvements (still JSF, enhanced interactions)
> Better workflows within JSF, and prep work for React migration.

- [ ] **2.1 — Dashboard landing page**: Job status overview, recent projects, quick actions
- [ ] **2.2 — Breadcrumb navigation**: Context-aware breadcrumbs across all pages
- [ ] **2.3 — Improved filtering**: Better per-table filters, saved filter presets
- [ ] **2.4 — Script editor UX**: Better step visualization, inline preview
- [ ] **2.5 — Agent tracker cards**: Status cards with progress visualization
- [ ] **2.6 — Dark mode**: Theme toggle using the CSS custom properties from Phase 1

### Phase 3: API Gap Filling (backend, SPA readiness)
> Every UI action must have a REST endpoint before we can migrate that page off JSF.

- [ ] **3.1 — User management API**: `/v2/users` CRUD + permissions
- [ ] **3.2 — Script editor API**: Step CRUD, reorder, validate, copy/paste
- [ ] **3.3 — Reporting API**: TPS data, test results, historical metrics
- [ ] **3.4 — Dashboard API**: Summary stats, recent activity, job aggregation
- [ ] **3.5 — Live monitoring**: WebSocket/SSE endpoints for real-time metrics
- [ ] **3.6 — Settings API**: User preferences, display settings

### Phase 4: React SPA Migration (Strangler Fig, page-by-page)
> Replace JSF pages with PrimeReact equivalents, one section at a time.

- [ ] **4.1 — React project setup**: Vite + TypeScript + PrimeReact + TanStack Query + React Router
- [ ] **4.2 — Shared layout shell**: Header nav, sidebar (if added), auth context — matching the JSF layout exactly
- [ ] **4.3 — Data Files page** (simplest CRUD table — proof of concept)
- [ ] **4.4 — Filters page** (CRUD with groups)
- [ ] **4.5 — Projects list** (data table + actions + job queue)
- [ ] **4.6 — Agent Tracker** (real-time — WebSocket integration)
- [ ] **4.7 — Admin pages** (user management, logs)
- [ ] **4.8 — Script Editor** (most complex — last to migrate)
- [ ] **4.9 — Login/auth flow**: Token-based auth with login/logout/refresh
- [ ] **4.10 — JSF deprecation**: Remove JSF modules once all pages migrated

---

## Design Principles
1. **Be bold**: <100 users — they'll adapt. Don't shy away from design improvements and easy wins. Search the web for modern patterns and apply them.
2. **Don't break functionality**: The app must still work — but visual/layout changes are fair game
3. **Incremental delivery**: Every change ships independently and works with the existing system
4. **Data-dense, not cluttered**: Load testing UIs need to show lots of data clearly (tables, metrics, logs)
5. **Fast feedback**: Loading states, optimistic updates, minimal page reloads
6. **Professional aesthetic**: Clean, flat, modern — think Grafana, Datadog, Gatling Enterprise
7. **Make it look like it was built this year**: If something looks dated, fix it — don't preserve it out of caution

## Build, Deploy & Run

### Quick reference
```bash
# 1. Build + deploy WAR to Tomcat (one command)
./deploy-local.sh

# 2. Start Tomcat (foreground — see logs live)
../apache-tomcat-10.1.16/bin/catalina.sh run

# 3. Open in browser
# http://localhost:8080/tank
```

### Details
- **Deploy script**: `./deploy-local.sh` — builds with `mvn clean install -P release -DskipITs -DskipTests`, then copies `web/web_ui/target/tank.war` to Tomcat webapps
- **Tomcat directory**: `../apache-tomcat-10.1.16/` (relative to project root, i.e. `/Users/zkofiro/Code/apache-tomcat-10.1.16/`)
- **Start server**: `../apache-tomcat-10.1.16/bin/catalina.sh run` (foreground, Ctrl+C to stop)
- **Run a single test**: `mvn test -pl <module> -Dtest=<Class>#<method>`
- **Proxy jar** (if needed): `proxy-parent/proxy_pkg/target/Tank-Proxy-all.jar`

### After making UI changes
Always deploy and run to manually validate:
```bash
./deploy-local.sh && ../apache-tomcat-10.1.16/bin/catalina.sh run
```

**Important**: Partial `mvn install -pl` does NOT reassemble fat jars or the WAR — always use `./deploy-local.sh` or the full `mvn clean install -P release -DskipITs -DskipTests`.
