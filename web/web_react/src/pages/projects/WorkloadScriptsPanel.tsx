import { useState, useRef, useEffect } from 'react';
import { TabView, TabPanel } from 'primereact/tabview';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { useProject, useUpdateProject } from '../../hooks/useProjects';
import { useScripts } from '../../hooks/useScripts';
import type { AutomationTestPlan, AutomationScriptGroup, AutomationScriptGroupStep } from '../../types/project';

interface Props {
  projectId: number;
}

export function WorkloadScriptsPanel({ projectId }: Props) {
  const { data: project } = useProject(projectId);
  const updateProject = useUpdateProject(projectId);
  const { data: allScripts } = useScripts();
  const toast = useRef<Toast>(null);

  const [testPlans, setTestPlans] = useState<AutomationTestPlan[]>([]);
  const [activeIndex, setActiveIndex] = useState(0);

  // Add test plan dialog
  const [showAddPlan, setShowAddPlan] = useState(false);
  const [newPlanName, setNewPlanName] = useState('');
  const [newPlanPct, setNewPlanPct] = useState<number>(100);

  // Edit script group dialog
  const [showEditGroup, setShowEditGroup] = useState(false);
  const [editGroupPlanIdx, setEditGroupPlanIdx] = useState(0);
  const [editGroupIdx, setEditGroupIdx] = useState<number | null>(null);
  const [editGroupName, setEditGroupName] = useState('');
  const [editGroupLoop, setEditGroupLoop] = useState(1);
  const [editGroupScripts, setEditGroupScripts] = useState<AutomationScriptGroupStep[]>([]);
  const [insertBeforeIdx, setInsertBeforeIdx] = useState<number | null>(null);

  // Script picker dialog
  const [showScriptPicker, setShowScriptPicker] = useState(false);
  const [scriptFilter, setScriptFilter] = useState('');

  useEffect(() => {
    if (project?.testPlans) {
      setTestPlans(JSON.parse(JSON.stringify(project.testPlans)));
    }
  }, [project?.testPlans]);

  async function save(plans: AutomationTestPlan[]) {
    if (!project) return;
    try {
      await updateProject.mutateAsync({
        name: project.name,
        productName: project.productName,
        comments: project.comments,
        rampTime: project.rampTime,
        simulationTime: project.simulationTime != null ? String(project.simulationTime) : undefined,
        location: project.location,
        stopBehavior: project.stopBehavior,
        testPlans: plans,
      });
      toast.current?.show({ severity: 'success', summary: 'Saved' });
    } catch {
      toast.current?.show({ severity: 'error', summary: 'Save failed' });
    }
  }

  function handleAddPlan() {
    const plan: AutomationTestPlan = {
      name: newPlanName.trim(),
      userPercentage: newPlanPct,
      scriptGroups: [],
    };
    const updated = [...testPlans, plan];
    setTestPlans(updated);
    setActiveIndex(updated.length - 1);
    setShowAddPlan(false);
    setNewPlanName('');
    setNewPlanPct(100);
    save(updated);
  }

  function handleDeletePlan(planIdx: number) {
    confirmDialog({
      message: `Delete test plan "${testPlans[planIdx].name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: () => {
        const updated = testPlans.filter((_, i) => i !== planIdx);
        setTestPlans(updated);
        setActiveIndex(Math.max(0, activeIndex - 1));
        save(updated);
      },
    });
  }

  function handlePlanNameBlur(planIdx: number, value: string) {
    if (!value.trim()) return;
    const updated = testPlans.map((p, i) => i === planIdx ? { ...p, name: value.trim() } : p);
    setTestPlans(updated);
    save(updated);
  }

  function handlePlanPctBlur(planIdx: number, value: number | null) {
    if (value == null) return;
    const updated = testPlans.map((p, i) => i === planIdx ? { ...p, userPercentage: value } : p);
    setTestPlans(updated);
    save(updated);
  }

  function openInsertGroup(planIdx: number, beforeIdx: number | null) {
    setEditGroupPlanIdx(planIdx);
    setEditGroupIdx(null);
    setInsertBeforeIdx(beforeIdx);
    setEditGroupName('');
    setEditGroupLoop(1);
    setEditGroupScripts([]);
    setShowEditGroup(true);
  }

  function openEditGroup(planIdx: number, groupIdx: number) {
    const group = testPlans[planIdx].scriptGroups![groupIdx];
    setEditGroupPlanIdx(planIdx);
    setEditGroupIdx(groupIdx);
    setInsertBeforeIdx(null);
    setEditGroupName(group.name);
    setEditGroupLoop(group.loop);
    setEditGroupScripts(JSON.parse(JSON.stringify(group.scripts ?? [])));
    setShowEditGroup(true);
  }

  function handleSaveGroup() {
    if (!editGroupName.trim()) return;
    const group: AutomationScriptGroup = {
      name: editGroupName.trim(),
      loop: editGroupLoop,
      scripts: editGroupScripts,
    };
    const updated = testPlans.map((plan, pi) => {
      if (pi !== editGroupPlanIdx) return plan;
      const groups = [...(plan.scriptGroups ?? [])];
      if (editGroupIdx !== null) {
        groups[editGroupIdx] = group;
      } else if (insertBeforeIdx !== null) {
        groups.splice(insertBeforeIdx, 0, group);
      } else {
        groups.push(group);
      }
      return { ...plan, scriptGroups: groups };
    });
    setTestPlans(updated);
    setShowEditGroup(false);
    save(updated);
  }

  function handleDeleteGroup(planIdx: number, groupIdx: number) {
    confirmDialog({
      message: `Delete script group "${testPlans[planIdx].scriptGroups![groupIdx].name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: () => {
        const updated = testPlans.map((plan, pi) => {
          if (pi !== planIdx) return plan;
          return { ...plan, scriptGroups: plan.scriptGroups!.filter((_, i) => i !== groupIdx) };
        });
        setTestPlans(updated);
        save(updated);
      },
    });
  }

  function handleGroupLoopChange(planIdx: number, groupIdx: number, value: number | null) {
    if (value == null) return;
    const updated = testPlans.map((plan, pi) => {
      if (pi !== planIdx) return plan;
      const groups = (plan.scriptGroups ?? []).map((g, gi) =>
        gi === groupIdx ? { ...g, loop: value } : g
      );
      return { ...plan, scriptGroups: groups };
    });
    setTestPlans(updated);
    save(updated);
  }

  function handleAddScriptFromPicker(scriptId: number, scriptName: string) {
    setEditGroupScripts([...editGroupScripts, { scriptId, scriptName, loop: 1 }]);
    setShowScriptPicker(false);
    setScriptFilter('');
  }

  function handleRemoveScript(idx: number) {
    setEditGroupScripts(editGroupScripts.filter((_, i) => i !== idx));
  }

  function handleDownloadHarness() {
    window.location.href = `/v2/projects/download/${projectId}`;
  }

  const filteredScripts = (allScripts ?? []).filter(
    (s) => !scriptFilter || s.name.toLowerCase().includes(scriptFilter.toLowerCase())
  );

  const addPlanFooter = (
    <div className="flex gap-2 justify-content-end">
      <Button label="Cancel" text onClick={() => setShowAddPlan(false)} />
      <Button label="Add" onClick={handleAddPlan} disabled={!newPlanName.trim()} />
    </div>
  );

  const editGroupFooter = (
    <div className="flex gap-2 justify-content-end">
      <Button label="Cancel" text onClick={() => setShowEditGroup(false)} />
      <Button label="Save" onClick={handleSaveGroup} disabled={!editGroupName.trim()} loading={updateProject.isPending} />
    </div>
  );

  if (!testPlans.length) {
    return (
      <div>
        <Toast ref={toast} />
        <div className="flex gap-2 mb-3">
          <Button icon="pi pi-plus" label="Add Test Plan" size="small" onClick={() => setShowAddPlan(true)} />
          <Button icon="pi pi-download" label="Harness XML" size="small" severity="secondary" onClick={handleDownloadHarness} />
        </div>
        <span className="text-color-secondary">No test plans configured.</span>

        <Dialog header="Add Test Plan" visible={showAddPlan} style={{ width: '350px' }} onHide={() => setShowAddPlan(false)} footer={addPlanFooter}>
          <div className="flex flex-column gap-3">
            <div className="flex flex-column gap-1">
              <label className="font-semibold">Name *</label>
              <InputText value={newPlanName} onChange={(e) => setNewPlanName(e.target.value)} autoFocus />
            </div>
            <div className="flex flex-column gap-1">
              <label className="font-semibold">User Percentage</label>
              <InputNumber value={newPlanPct} onValueChange={(e) => setNewPlanPct(e.value ?? 100)} min={0} max={100} suffix="%" />
            </div>
          </div>
        </Dialog>
      </div>
    );
  }

  return (
    <div>
      <Toast ref={toast} />
      <ConfirmDialog />

      <div className="flex gap-2 mb-3">
        <Button icon="pi pi-plus" label="Add Test Plan" size="small" onClick={() => setShowAddPlan(true)} />
        <Button icon="pi pi-download" label="Harness XML" size="small" severity="secondary" onClick={handleDownloadHarness} />
      </div>

      <TabView activeIndex={activeIndex} onTabChange={(e) => setActiveIndex(e.index)}>
        {testPlans.map((plan, planIdx) => (
          <TabPanel key={planIdx} header={`${plan.name} (${plan.userPercentage}%)`}>
            <div className="flex flex-column gap-3">
              <div className="grid">
                <div className="col-12 md:col-6">
                  <div className="flex flex-column gap-1">
                    <label className="font-semibold">Name</label>
                    <InputText
                      defaultValue={plan.name}
                      onBlur={(e) => handlePlanNameBlur(planIdx, e.target.value)}
                    />
                  </div>
                </div>
                <div className="col-12 md:col-3">
                  <div className="flex flex-column gap-1">
                    <label className="font-semibold">User Percentage</label>
                    <InputNumber
                      value={plan.userPercentage}
                      onBlur={(e) => handlePlanPctBlur(planIdx, Number(e.target.value))}
                      min={0}
                      max={100}
                      suffix="%"
                    />
                  </div>
                </div>
              </div>

              <div className="flex gap-2">
                <Button
                  icon="pi pi-plus"
                  label="Insert Script Group"
                  size="small"
                  onClick={() => openInsertGroup(planIdx, null)}
                />
                <Button
                  icon="pi pi-trash"
                  label="Delete Test Plan"
                  size="small"
                  severity="danger"
                  text
                  disabled={testPlans.length <= 1}
                  onClick={() => handleDeletePlan(planIdx)}
                />
              </div>

              <DataTable
                value={plan.scriptGroups ?? []}
                emptyMessage="No script groups. Use 'Insert Script Group' above."
                size="small"
              >
                <Column
                  header="Name"
                  body={(row: AutomationScriptGroup, opts) => (
                    <Button
                      link
                      label={row.name}
                      onClick={() => openEditGroup(planIdx, opts.rowIndex)}
                    />
                  )}
                />
                <Column
                  header="Loop"
                  style={{ width: '120px' }}
                  body={(row: AutomationScriptGroup, opts) => (
                    <InputNumber
                      value={row.loop}
                      onValueChange={(e) => handleGroupLoopChange(planIdx, opts.rowIndex, e.value ?? 1)}
                      min={1}
                      style={{ width: '80px' }}
                    />
                  )}
                />
                <Column
                  header="Scripts"
                  style={{ width: '80px' }}
                  body={(row: AutomationScriptGroup) => row.scripts?.length ?? 0}
                />
                <Column
                  header=""
                  style={{ width: '120px' }}
                  body={(row: AutomationScriptGroup, opts) => (
                    <div className="flex gap-1">
                      <Button
                        icon="pi pi-pencil"
                        rounded
                        text
                        size="small"
                        title={`Edit ${row.name}`}
                        onClick={() => openEditGroup(planIdx, opts.rowIndex)}
                      />
                      <Button
                        icon="pi pi-plus-circle"
                        rounded
                        text
                        size="small"
                        title={`Insert before ${row.name}`}
                        onClick={() => openInsertGroup(planIdx, opts.rowIndex)}
                      />
                      <Button
                        icon="pi pi-trash"
                        rounded
                        text
                        size="small"
                        severity="danger"
                        title={`Delete ${row.name}`}
                        onClick={() => handleDeleteGroup(planIdx, opts.rowIndex)}
                      />
                    </div>
                  )}
                />
              </DataTable>
            </div>
          </TabPanel>
        ))}
      </TabView>

      {/* Add Test Plan Dialog */}
      <Dialog header="Add Test Plan" visible={showAddPlan} style={{ width: '350px' }} onHide={() => setShowAddPlan(false)} footer={addPlanFooter}>
        <div className="flex flex-column gap-3">
          <div className="flex flex-column gap-1">
            <label className="font-semibold">Name *</label>
            <InputText value={newPlanName} onChange={(e) => setNewPlanName(e.target.value)} autoFocus />
          </div>
          <div className="flex flex-column gap-1">
            <label className="font-semibold">User Percentage</label>
            <InputNumber value={newPlanPct} onValueChange={(e) => setNewPlanPct(e.value ?? 100)} min={0} max={100} suffix="%" />
          </div>
        </div>
      </Dialog>

      {/* Edit/Add Script Group Dialog */}
      <Dialog
        header={editGroupIdx !== null ? 'Edit Script Group' : 'Add Script Group'}
        visible={showEditGroup}
        style={{ width: '550px' }}
        onHide={() => setShowEditGroup(false)}
        footer={editGroupFooter}
      >
        <div className="flex flex-column gap-3">
          <div className="grid">
            <div className="col-8">
              <div className="flex flex-column gap-1">
                <label className="font-semibold">Name *</label>
                <InputText value={editGroupName} onChange={(e) => setEditGroupName(e.target.value)} autoFocus />
              </div>
            </div>
            <div className="col-4">
              <div className="flex flex-column gap-1">
                <label className="font-semibold">Loop</label>
                <InputNumber value={editGroupLoop} onValueChange={(e) => setEditGroupLoop(e.value ?? 1)} min={1} />
              </div>
            </div>
          </div>

          <div>
            <div className="flex align-items-center justify-content-between mb-2">
              <span className="font-semibold">Scripts</span>
              <Button icon="pi pi-plus" label="Add Script" size="small" onClick={() => setShowScriptPicker(true)} />
            </div>
            <DataTable value={editGroupScripts} emptyMessage="No scripts." size="small">
              <Column field="scriptName" header="Script" />
              <Column
                header="Loop"
                style={{ width: '100px' }}
                body={(row: AutomationScriptGroupStep, opts) => (
                  <InputNumber
                    value={row.loop ?? 1}
                    onValueChange={(e) => {
                      const updated = editGroupScripts.map((s, i) =>
                        i === opts.rowIndex ? { ...s, loop: e.value ?? 1 } : s
                      );
                      setEditGroupScripts(updated);
                    }}
                    min={1}
                    style={{ width: '70px' }}
                  />
                )}
              />
              <Column
                header=""
                style={{ width: '50px' }}
                body={(_: AutomationScriptGroupStep, opts) => (
                  <Button
                    icon="pi pi-trash"
                    rounded
                    text
                    size="small"
                    severity="danger"
                    onClick={() => handleRemoveScript(opts.rowIndex)}
                  />
                )}
              />
            </DataTable>
          </div>
        </div>
      </Dialog>

      {/* Script Picker Dialog */}
      <Dialog
        header="Pick a Script"
        visible={showScriptPicker}
        style={{ width: '500px' }}
        onHide={() => { setShowScriptPicker(false); setScriptFilter(''); }}
      >
        <div className="flex flex-column gap-2">
          <InputText
            placeholder="Filter scripts..."
            value={scriptFilter}
            onChange={(e) => setScriptFilter(e.target.value)}
            autoFocus
          />
          <DataTable
            value={filteredScripts}
            selectionMode="single"
            onRowSelect={(e) => handleAddScriptFromPicker(e.data.id, e.data.name)}
            emptyMessage="No scripts found."
            size="small"
            scrollable
            scrollHeight="350px"
          >
            <Column field="name" header="Name" />
            <Column field="productName" header="Product" />
          </DataTable>
        </div>
      </Dialog>
    </div>
  );
}
