import { useState, useEffect } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Dropdown } from 'primereact/dropdown';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import type { ProjectTO, AutomationRequest, JobRegionTO } from '../../types/project';

const WORKLOAD_TYPE_OPTIONS = [
  { label: 'Linear (Increasing)', value: 'increasing' },
  { label: 'Nonlinear (Standard)', value: 'standard' },
];

const TERMINATION_POLICY_OPTIONS = [
  { label: 'Simulation Time Reached', value: 'time' },
  { label: 'Script Loops Completed', value: 'script' },
];

const REGION_OPTIONS = [
  { label: 'US East 1 (N. Virginia)', value: 'US_EAST_1' },
  { label: 'US East 2 (Ohio)', value: 'US_EAST_2' },
  { label: 'US West 1 (N. California)', value: 'US_WEST_1' },
  { label: 'US West 2 (Oregon)', value: 'US_WEST_2' },
  { label: 'EU West 1 (Ireland)', value: 'EU_WEST_1' },
  { label: 'AP Southeast 1 (Singapore)', value: 'AP_SOUTHEAST_1' },
  { label: 'Standalone', value: 'STANDALONE' },
];

interface Props {
  visible: boolean;
  project: ProjectTO;
  saving: boolean;
  onHide: () => void;
  onSave: (patch: Partial<AutomationRequest>) => void;
}

/** Convert milliseconds (from API) to time expression string for display/editing */
function msToExpr(ms?: number): string {
  if (!ms) return '';
  const totalSec = Math.round(ms / 1000);
  const h = Math.floor(totalSec / 3600);
  const m = Math.floor((totalSec % 3600) / 60);
  const s = totalSec % 60;
  if (h > 0) return `${h}h${m > 0 ? ` ${m}m` : ''}`;
  if (m > 0) return `${m}m${s > 0 ? ` ${s}s` : ''}`;
  return `${s}s`;
}

export function UsersAndTimesDialog({ visible, project, saving, onHide, onSave }: Props) {
  const [rampTime, setRampTime]                     = useState('');
  const [simulationTime, setSimulationTime]         = useState('');
  const [userIntervalIncrement, setUserIntervalIncrement] = useState<number>(1);
  const [workloadType, setWorkloadType]             = useState('increasing');
  const [terminationPolicy, setTerminationPolicy]   = useState('time');
  const [jobRegions, setJobRegions]                 = useState<JobRegionTO[]>([]);

  // Reset fields from project whenever dialog opens
  useEffect(() => {
    if (visible) {
      setRampTime(project.rampTime ?? msToExpr(undefined));
      setSimulationTime(
        project.simulationTime != null ? msToExpr(project.simulationTime) : ''
      );
      setUserIntervalIncrement(project.userIntervalIncrement ?? 1);
      setWorkloadType((project as any).workloadType ?? 'increasing');
      setTerminationPolicy((project as any).terminationPolicy ?? 'time');
      setJobRegions(
        project.jobRegions && project.jobRegions.length > 0
          ? project.jobRegions.map((r) => ({ ...r }))
          : [{ region: 'US_EAST_1', users: '100', percentage: '100' }]
      );
    }
  }, [visible, project]);

  const addRegion = () =>
    setJobRegions((prev) => [...prev, { region: 'US_EAST_1', users: '0', percentage: '0' }]);

  const removeRegion = (idx: number) =>
    setJobRegions((prev) => prev.filter((_, i) => i !== idx));

  const updateRegionField = (idx: number, field: keyof JobRegionTO, value: string) =>
    setJobRegions((prev) =>
      prev.map((r, i) => (i === idx ? { ...r, [field]: value } : r))
    );

  const handleSave = () => {
    onSave({
      rampTime: rampTime.trim() || undefined,
      simulationTime: simulationTime.trim() || undefined,
      userIntervalIncrement,
      workloadType,
      terminationPolicy,
      jobRegions,
    });
  };

  const isLinear = workloadType === 'increasing';

  return (
    <Dialog
      header="Users & Times Configuration"
      visible={visible}
      style={{ width: '560px' }}
      onHide={onHide}
      footer={
        <div className="flex justify-content-end gap-2">
          <Button label="Cancel" severity="secondary" onClick={onHide} />
          <Button label="Save" icon="pi pi-check" loading={saving} onClick={handleSave} />
        </div>
      }
    >
      <div className="flex flex-column gap-4 p-fluid">

        {/* Workload Type */}
        <div className="field">
          <label className="font-bold">Workload Type</label>
          <Dropdown
            value={workloadType}
            options={WORKLOAD_TYPE_OPTIONS}
            onChange={(e) => setWorkloadType(e.value)}
          />
        </div>

        {/* Termination Policy */}
        {isLinear && (
          <div className="field">
            <label className="font-bold">Run Scripts Until</label>
            <Dropdown
              value={terminationPolicy}
              options={TERMINATION_POLICY_OPTIONS}
              onChange={(e) => setTerminationPolicy(e.value)}
            />
          </div>
        )}

        {/* Ramp Time */}
        <div className="field">
          <label className="font-bold">Ramp Time</label>
          <InputText
            value={rampTime}
            onChange={(e) => setRampTime(e.target.value)}
            placeholder="e.g. 5m, 60s, 1h30m"
          />
          <small className="text-color-secondary">
            Time expressions: 60s, 5m, 1h, 1h30m
          </small>
        </div>

        {/* Simulation Time */}
        <div className="field">
          <label className="font-bold">Simulation Time</label>
          <InputText
            value={simulationTime}
            onChange={(e) => setSimulationTime(e.target.value)}
            placeholder="e.g. 30m, 1h, 2h30m"
          />
        </div>

        {/* User Interval Increment */}
        {isLinear && (
          <div className="field">
            <label className="font-bold">User Increment (per ramp interval)</label>
            <InputNumber
              value={userIntervalIncrement}
              onValueChange={(e) => setUserIntervalIncrement(e.value ?? 1)}
              min={1}
              showButtons
            />
          </div>
        )}

        {/* Job Regions */}
        <div className="field">
          <div className="flex align-items-center justify-content-between mb-2">
            <label className="font-bold mb-0">
              {isLinear ? 'Regions & User Distribution' : 'Regions & Percentages'}
            </label>
            <Button label="Add Region" icon="pi pi-plus" size="small" severity="secondary" onClick={addRegion} />
          </div>
          <DataTable value={jobRegions} showGridlines stripedRows emptyMessage="No regions configured.">
            <Column
              header="Region"
              body={(_, { rowIndex }) => (
                <Dropdown
                  value={jobRegions[rowIndex].region}
                  options={REGION_OPTIONS}
                  onChange={(e) => updateRegionField(rowIndex, 'region', e.value)}
                  className="w-full"
                />
              )}
            />
            {isLinear ? (
              <Column
                header="Users"
                style={{ width: '120px' }}
                body={(_, { rowIndex }) => (
                  <InputText
                    value={jobRegions[rowIndex].users ?? ''}
                    onChange={(e) => updateRegionField(rowIndex, 'users', e.target.value)}
                    placeholder="100"
                  />
                )}
              />
            ) : (
              <Column
                header="Percentage"
                style={{ width: '120px' }}
                body={(_, { rowIndex }) => (
                  <InputText
                    value={jobRegions[rowIndex].percentage ?? ''}
                    onChange={(e) => updateRegionField(rowIndex, 'percentage', e.target.value)}
                    placeholder="100"
                  />
                )}
              />
            )}
            <Column
              style={{ width: '50px' }}
              body={(_, { rowIndex }) => (
                <Button
                  icon="pi pi-trash"
                  size="small"
                  text
                  severity="danger"
                  disabled={jobRegions.length <= 1}
                  onClick={() => removeRegion(rowIndex)}
                />
              )}
            />
          </DataTable>
        </div>

      </div>
    </Dialog>
  );
}
