import { Link } from 'react-router';
import { useSession } from '../session';

/** The path from a recorded session to a running test, in the order users take it */
const STEPS = [
  {
    icon: 'pi pi-video',
    title: 'Record',
    text: 'Browse your app through Tank Proxy, or upload a recorded script, and every request becomes a step.',
  },
  {
    icon: 'pi pi-sliders-h',
    title: 'Shape',
    text: 'Filter out noise, pull values from responses into variables and feed each user its own test data.',
  },
  {
    icon: 'pi pi-users',
    title: 'Plan',
    text: 'Group scripts into a project, then set the users, ramp, duration and regions the test runs with.',
  },
  {
    icon: 'pi pi-chart-line',
    title: 'Run',
    text: 'Tank starts agents on EC2, drives the load and streams users, TPS and failures back as it runs.',
  },
];

const FEATURES = [
  {
    icon: 'pi pi-folder',
    title: 'Projects that model real traffic',
    text:
      'Ramp users up linearly or hold a target rate of users per second. Split load across regions, ' +
      'weight script groups by share of users, and stop on a timer or when scripts finish.',
    path: '/projects',
    cta: 'Plan a test',
  },
  {
    icon: 'pi pi-file-edit',
    title: 'Scripts you can read and edit',
    text:
      'Requests, think and sleep times, cookies, authentication, variables and JavaScript logic steps, ' +
      'with response validation and search and replace across every step.',
    path: '/scripts',
    cta: 'Open scripts',
  },
  {
    icon: 'pi pi-filter',
    title: 'Filters that clean up recordings',
    text:
      'Drop analytics calls and static assets, or rewrite hosts and headers, as a script is imported. ' +
      'Group filters to apply a whole policy at once.',
    path: '/filters',
    cta: 'Build a filter',
  },
  {
    icon: 'pi pi-database',
    title: 'Data files for realistic users',
    text:
      'Upload CSV logins, accounts or search terms and Tank divides the lines evenly among agents, ' +
      'so virtual users work through real, varied data.',
    path: '/datafiles',
    cta: 'Upload data',
  },
  {
    icon: 'pi pi-server',
    title: 'Live control of every agent',
    text:
      'Watch active users and transactions per second per job or per agent. Pause the ramp, pause, ' +
      'resume, stop or kill a test while it runs.',
    path: '/jobs',
    cta: 'Track agents',
  },
  {
    icon: 'pi pi-wrench',
    title: 'Tools for your desktop',
    text:
      'Record with Tank Proxy, step through a script request by request in the visual debugger, ' +
      'or replay a project headless from the command line.',
    path: '/tools',
    cta: 'Get the tools',
  },
];

/** What sets Tank apart, shown on the hero as quick proof points */
const HIGHLIGHTS = [
  { icon: 'pi pi-cloud', text: 'Agents on demand in AWS' },
  { icon: 'pi pi-globe', text: 'Load from multiple regions' },
  { icon: 'pi pi-code', text: 'A REST API for automation' },
];

export function HomePage() {
  const { user } = useSession();
  return (
    <div className="home">
      <section className="home-hero">
        <div className="home-hero-text">
          <p className="home-greeting">Welcome back, {user?.name}</p>
          <h1>Find your breaking point before your customers do.</h1>
          <p className="home-lead">
            Tank records real user journeys and replays them at scale from cloud agents, so you see how your
            service holds up under load, and where it doesn’t, before launch day.
          </p>
          <div className="home-actions">
            <Link to="/projects" className="home-button home-button-primary">
              <i className="pi pi-play" aria-hidden /> Start a load test
            </Link>
            <Link to="/jobs" className="home-button">
              <i className="pi pi-server" aria-hidden /> See running tests
            </Link>
          </div>
          <ul className="home-highlights">
            {HIGHLIGHTS.map((h) => (
              <li key={h.text}>
                <i className={h.icon} aria-hidden /> {h.text}
              </li>
            ))}
          </ul>
        </div>
        <HeroChart />
      </section>

      <section className="home-section" aria-labelledby="home-how">
        <h2 id="home-how">From recording to results in four steps</h2>
        <ol className="home-steps">
          {STEPS.map((step, index) => (
            <li key={step.title} className="home-step">
              <span className="home-step-number" aria-hidden>
                {index + 1}
              </span>
              <i className={`${step.icon} home-step-icon`} aria-hidden />
              <h3>{step.title}</h3>
              <p>{step.text}</p>
            </li>
          ))}
        </ol>
      </section>

      <section className="home-section" aria-labelledby="home-features">
        <h2 id="home-features">Everything a load test needs</h2>
        <div className="home-features">
          {FEATURES.map((f) => (
            <Link key={f.path} to={f.path} className="home-feature">
              <i className={`${f.icon} home-feature-icon`} aria-hidden />
              <h3>{f.title}</h3>
              <p>{f.text}</p>
              <span className="home-feature-cta">
                {f.cta} <i className="pi pi-arrow-right" aria-hidden />
              </span>
            </Link>
          ))}
        </div>
      </section>

      <section className="home-callout">
        <div>
          <h2>Load testing in your pipeline</h2>
          <p>
            Everything here is also in Tank’s REST API. Create an API token on your account page and start
            tests, check their status and stop them from CI.
          </p>
        </div>
        <Link to="/account" className="home-button home-button-primary">
          <i className="pi pi-key" aria-hidden /> Get an API token
        </Link>
      </section>
    </div>
  );
}

/** A decorative ramp: users climbing to a plateau, with TPS following them */
function HeroChart() {
  return (
    <svg className="home-hero-chart" viewBox="0 0 320 200" aria-hidden focusable="false">
      {[40, 80, 120, 160].map((y) => (
        <line key={y} x1="0" x2="320" y1={y} y2={y} className="home-chart-grid" />
      ))}
      <path d="M0 180 L120 60 L320 60 L320 200 L0 200 Z" className="home-chart-area" />
      <path d="M0 180 L120 60 L320 60" className="home-chart-users" />
      <path
        d="M0 186 L20 170 L40 150 L60 132 L80 110 L100 96 L120 82 L140 90 L160 78 L180 86 L200 74 L220 84 L240 76 L260 88 L280 72 L300 82 L320 78"
        className="home-chart-tps"
      />
      <line x1="8" x2="24" y1="16" y2="16" className="home-chart-users" />
      <text x="30" y="20" className="home-chart-label">
        Users
      </text>
      <line x1="80" x2="96" y1="16" y2="16" className="home-chart-tps home-chart-key" />
      <text x="102" y="20" className="home-chart-label">
        TPS
      </text>
    </svg>
  );
}
