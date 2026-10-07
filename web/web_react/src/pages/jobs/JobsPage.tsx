import { JobQueue } from './JobQueue';

/** Every project's recent and running jobs (agents/index.xhtml) */
export function JobsPage() {
  return (
    <section className="list-page">
      <div className="page-header">
        <h1>Job queue</h1>
      </div>
      <JobQueue />
    </section>
  );
}
