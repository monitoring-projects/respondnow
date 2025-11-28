/* eslint-disable */
import { useMutation, UseMutationOptions } from '@tanstack/react-query';
import { fetcher, FetcherOptions } from '@services/fetcher';
import type { Incident } from '../schemas/Incident';

export interface UpdateIncidentStatusRequestBody {
  status: 'Started' | 'Acknowledged' | 'Investigating' | 'Identified' | 'Mitigated' | 'Resolved';
}

export interface UpdateIncidentStatusQueryParams {
  accountIdentifier: string;
}

export interface UpdateIncidentStatusOkResponse {
  status: string;
  message: string;
  data?: Incident;
}

export type UpdateIncidentStatusErrorResponse = UpdateIncidentStatusOkResponse;

export interface UpdateIncidentStatusProps extends Omit<FetcherOptions<UpdateIncidentStatusQueryParams, UpdateIncidentStatusRequestBody>, 'url'> {
  incidentIdentifier: string;
  body: UpdateIncidentStatusRequestBody;
  queryParams: UpdateIncidentStatusQueryParams;
}

export function updateIncidentStatus(props: UpdateIncidentStatusProps): Promise<UpdateIncidentStatusOkResponse> {
  const { incidentIdentifier, ...rest } = props;
  return fetcher<UpdateIncidentStatusOkResponse, UpdateIncidentStatusQueryParams, UpdateIncidentStatusRequestBody>({
    url: `/api/incident/${incidentIdentifier}/status`,
    method: 'PUT',
    ...rest
  });
}

export type UpdateIncidentStatusMutationProps<T extends keyof UpdateIncidentStatusProps> = Omit<UpdateIncidentStatusProps, T> &
  Partial<Pick<UpdateIncidentStatusProps, T>>;

/**
 * Update incident status
 */
export function useUpdateIncidentStatusMutation<T extends keyof UpdateIncidentStatusProps>(
  props: Pick<Partial<UpdateIncidentStatusProps>, T>,
  options?: Omit<
    UseMutationOptions<UpdateIncidentStatusOkResponse, UpdateIncidentStatusErrorResponse, UpdateIncidentStatusMutationProps<T>>,
    'mutationKey' | 'mutationFn'
  >
) {
  return useMutation<UpdateIncidentStatusOkResponse, UpdateIncidentStatusErrorResponse, UpdateIncidentStatusMutationProps<T>>(
    (mutateProps: UpdateIncidentStatusMutationProps<T>) => updateIncidentStatus({ ...props, ...mutateProps } as UpdateIncidentStatusProps),
    options
  );
}
