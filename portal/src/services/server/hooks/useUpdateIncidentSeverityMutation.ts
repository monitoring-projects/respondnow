/* eslint-disable */
import { useMutation, UseMutationOptions } from '@tanstack/react-query';
import { fetcher, FetcherOptions } from '@services/fetcher';
import type { Incident } from '../schemas/Incident';

export interface UpdateIncidentSeverityRequestBody {
  severity: 'SEV0' | 'SEV1' | 'SEV2';
}

export interface UpdateIncidentSeverityQueryParams {
  accountIdentifier: string;
}

export interface UpdateIncidentSeverityOkResponse {
  status: string;
  message: string;
  data?: Incident;
}

export type UpdateIncidentSeverityErrorResponse = UpdateIncidentSeverityOkResponse;

export interface UpdateIncidentSeverityProps extends Omit<FetcherOptions<UpdateIncidentSeverityQueryParams, UpdateIncidentSeverityRequestBody>, 'url'> {
  incidentIdentifier: string;
  body: UpdateIncidentSeverityRequestBody;
  queryParams: UpdateIncidentSeverityQueryParams;
}

export function updateIncidentSeverity(props: UpdateIncidentSeverityProps): Promise<UpdateIncidentSeverityOkResponse> {
  const { incidentIdentifier, ...rest } = props;
  return fetcher<UpdateIncidentSeverityOkResponse, UpdateIncidentSeverityQueryParams, UpdateIncidentSeverityRequestBody>({
    url: `/api/incident/${incidentIdentifier}/severity`,
    method: 'PUT',
    ...rest
  });
}

export type UpdateIncidentSeverityMutationProps<T extends keyof UpdateIncidentSeverityProps> = Omit<UpdateIncidentSeverityProps, T> &
  Partial<Pick<UpdateIncidentSeverityProps, T>>;

/**
 * Update incident severity
 */
export function useUpdateIncidentSeverityMutation<T extends keyof UpdateIncidentSeverityProps>(
  props: Pick<Partial<UpdateIncidentSeverityProps>, T>,
  options?: Omit<
    UseMutationOptions<UpdateIncidentSeverityOkResponse, UpdateIncidentSeverityErrorResponse, UpdateIncidentSeverityMutationProps<T>>,
    'mutationKey' | 'mutationFn'
  >
) {
  return useMutation<UpdateIncidentSeverityOkResponse, UpdateIncidentSeverityErrorResponse, UpdateIncidentSeverityMutationProps<T>>(
    (mutateProps: UpdateIncidentSeverityMutationProps<T>) => updateIncidentSeverity({ ...props, ...mutateProps } as UpdateIncidentSeverityProps),
    options
  );
}
