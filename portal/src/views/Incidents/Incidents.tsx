import React, { useState, useCallback } from 'react';
import { Button, ButtonVariation, FlexExpander, Layout, Pagination, Text, useToaster, DropDown, SelectOption } from '@harnessio/uicore';
import { Color } from '@harnessio/design-system';
import { DefaultLayout } from '@layouts';
import { useStrings } from '@strings';
import { IncidentsTableProps } from '@interfaces';
import MemoisedIncidentListTable from '@tables/Incidents/ListIncidentsTable';
import { useExportCSVMutation, useExportPDFMutation, downloadBlob, ExportRequestBody } from '@services/server';
import { getScope } from '@utils';

interface IncidentsViewProps {
  tableData: IncidentsTableProps;
  incidentsSearchBar: React.JSX.Element;
  incidentsStatusFilter: React.JSX.Element;
  incidentsSeverityFilter: React.JSX.Element;
  resetFiltersButton: React.JSX.Element;
  areFiltersSet: boolean;
  onCreateIncident?: () => void;
}

const IncidentsView: React.FC<IncidentsViewProps> = props => {
  const {
    tableData,
    incidentsSearchBar,
    incidentsStatusFilter,
    incidentsSeverityFilter,
    areFiltersSet,
    resetFiltersButton,
    onCreateIncident
  } = props;
  const { getString } = useStrings();
  const { showSuccess, showError } = useToaster();
  const scope = getScope();

  // Selection state
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
  const [isExportingCSV, setIsExportingCSV] = useState(false);
  const [isExportingPDF, setIsExportingPDF] = useState(false);

  const isDataPresent = !!tableData.content.length;
  const selectedCount = selectedIds.size;

  // CSV Export mutation
  const { mutate: exportCSV } = useExportCSVMutation(
    { queryParams: scope },
    {
      onSuccess: (response) => {
        const filename = `incidents_${new Date().toISOString().split('T')[0]}.csv`;
        downloadBlob(response.data, filename);
        showSuccess(`Exported ${response.totalCount} incidents to CSV`);
        setIsExportingCSV(false);
      },
      onError: () => {
        showError('Failed to export incidents to CSV');
        setIsExportingCSV(false);
      }
    }
  );

  // PDF Export mutation
  const { mutate: exportPDF } = useExportPDFMutation(
    { queryParams: scope },
    {
      onSuccess: (response) => {
        const filename = `incidents_${new Date().toISOString().split('T')[0]}.pdf`;
        downloadBlob(response.data, filename);
        showSuccess(`Exported ${response.totalCount} incidents to PDF`);
        setIsExportingPDF(false);
      },
      onError: () => {
        showError('Failed to export incidents to PDF');
        setIsExportingPDF(false);
      }
    }
  );

  const handleSelectionChange = useCallback((newSelection: Set<string>) => {
    setSelectedIds(newSelection);
  }, []);

  const handleExportSelectedCSV = () => {
    if (selectedCount === 0) {
      showError('Please select incidents to export');
      return;
    }
    setIsExportingCSV(true);
    const body: ExportRequestBody = {
      incidentIds: Array.from(selectedIds)
    };
    exportCSV(body);
  };

  const handleExportSelectedPDF = () => {
    if (selectedCount === 0) {
      showError('Please select incidents to export');
      return;
    }
    setIsExportingPDF(true);
    const body: ExportRequestBody = {
      incidentIds: Array.from(selectedIds)
    };
    exportPDF(body);
  };

  const handleExportAllCSV = () => {
    setIsExportingCSV(true);
    const body: ExportRequestBody = {
      exportAll: true
    };
    exportCSV(body);
  };

  const handleExportAllPDF = () => {
    setIsExportingPDF(true);
    const body: ExportRequestBody = {
      exportAll: true
    };
    exportPDF(body);
  };

  const handleClearSelection = () => {
    setSelectedIds(new Set());
  };

  const isExporting = isExportingCSV || isExportingPDF;

  // Export menu items for selected incidents
  const selectedExportOptions: SelectOption[] = [
    {
      label: 'Export as CSV',
      value: 'csv'
    },
    {
      label: 'Export as PDF',
      value: 'pdf'
    }
  ];

  const handleSelectedExportChange = (option: SelectOption) => {
    if (option.value === 'csv') {
      handleExportSelectedCSV();
    } else if (option.value === 'pdf') {
      handleExportSelectedPDF();
    }
  };

  // Selection toolbar shown when items are selected
  const selectionToolbar = selectedCount > 0 && (
    <Layout.Horizontal 
      spacing="medium" 
      flex={{ alignItems: 'center' }} 
      padding={{ left: 'medium', right: 'medium', top: 'small', bottom: 'small' }}
      style={{ backgroundColor: '#f3f3fa', borderRadius: '4px', marginBottom: '8px' }}
    >
      <Text color={Color.GREY_800} font={{ weight: 'semi-bold' }}>
        {selectedCount} incident{selectedCount > 1 ? 's' : ''} selected
      </Text>
      <DropDown
        items={selectedExportOptions}
        onChange={handleSelectedExportChange}
        disabled={isExporting}
        placeholder="Export Selected"
        filterable={false}
        icon="download-box"
      />
      <Button
        variation={ButtonVariation.LINK}
        size="small"
        text="Clear Selection"
        onClick={handleClearSelection}
      />
    </Layout.Horizontal>
  );

  // Export menu items for all incidents
  const exportAllOptions: SelectOption[] = [
    {
      label: 'Export All as CSV',
      value: 'csv'
    },
    {
      label: 'Export All as PDF',
      value: 'pdf'
    }
  ];

  const handleExportAllChange = (option: SelectOption) => {
    if (option.value === 'csv') {
      handleExportAllCSV();
    } else if (option.value === 'pdf') {
      handleExportAllPDF();
    }
  };

  const toolbar = (
    <Layout.Horizontal spacing="small">
      <DropDown
        items={exportAllOptions}
        onChange={handleExportAllChange}
        disabled={!isDataPresent || isExporting}
        placeholder="Export All"
        filterable={false}
        icon="download-box"
      />
      <Button
        variation={ButtonVariation.PRIMARY}
        text="Create Incident"
        icon="plus"
        onClick={onCreateIncident}
      />
    </Layout.Horizontal>
  );

  return (
    <DefaultLayout
      loading={tableData.isLoading}
      title={getString('incidents')}
      toolbar={toolbar}
      subHeader={
        <>
          <Layout.Horizontal flex={{ alignItems: 'center', justifyContent: 'flex-start' }} spacing="medium">
            {incidentsStatusFilter}
            {incidentsSeverityFilter}
          </Layout.Horizontal>
          <FlexExpander />
          {incidentsSearchBar}
        </>
      }
      footer={tableData.pagination && <Pagination {...tableData.pagination} />}
      noData={!isDataPresent}
      noDataProps={{
        height: 350,
        ...(areFiltersSet
          ? {
              title: getString('noIncidentsFoundMatchingFilters'),
              subtitle: getString('noIncidentsFoundMatchingFiltersDescription'),
              ctaButton: resetFiltersButton
            }
          : {
              title: getString('noIncidentsFound'),
              subtitle: getString('noIncidentsFoundDescription'),
              ctaButton: toolbar
            })
      }}
    >
      {selectionToolbar}
      <MemoisedIncidentListTable 
        content={tableData.content}
        selectable={true}
        selectedIds={selectedIds}
        onSelectionChange={handleSelectionChange}
      />
    </DefaultLayout>
  );
};

export default IncidentsView;
