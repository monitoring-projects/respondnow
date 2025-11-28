/* eslint-disable */
import { useMutation, UseMutationOptions } from '@tanstack/react-query';
import { fetcher, FetcherOptions } from '@services/fetcher';
import type { Incident } from '../schemas/Incident';

export interface AcknowledgeIncidentQueryParams {
  accountIdentifier: string;
}

export interface AcknowledgeIncidentOkResponse {
  status: string;
  message: string;
  data?: Incident;
}

export type AcknowledgeIncidentErrorResponse = AcknowledgeIncidentOkResponse;

export interface AcknowledgeIncidentProps extends Omit<FetcherOptions<AcknowledgeIncidentQueryParams, unknown>, 'url'> {
  incidentIdentifier: string;
  queryParams: AcknowledgeIncidentQueryParams;
}

export function acknowledgeIncident(props: AcknowledgeIncidentProps): Promise<AcknowledgeIncidentOkResponse> {
  const { incidentIdentifier, ...rest } = props;
  return fetcher<AcknowledgeIncidentOkResponse, AcknowledgeIncidentQueryParams, unknown>({
    url: `/api/incident/${incidentIdentifier}/acknowledge`,
    method: 'PUT',
    ...rest
  });
}

export type AcknowledgeIncidentMutationProps<T extends keyof AcknowledgeIncidentProps> = Omit<AcknowledgeIncidentProps, T> &
  Partial<Pick<AcknowledgeIncidentProps, T>>;

/**
 * Acknowledge an incident
 */
export function useAcknowledgeIncidentMutation<T extends keyof AcknowledgeIncidentProps>(
  props: Pick<Partial<AcknowledgeIncidentProps>, T>,
  options?: Omit<
    UseMutationOptions<AcknowledgeIncidentOkResponse, AcknowledgeIncidentErrorResponse, AcknowledgeIncidentMutationProps<T>>,
    'mutationKey' | 'mutationFn'
  >
) {
  return useMutation<AcknowledgeIncidentOkResponse, AcknowledgeIncidentErrorResponse, AcknowledgeIncidentMutationProps<T>>(
    (mutateProps: AcknowledgeIncidentMutationProps<T>) => acknowledgeIncident({ ...props, ...mutateProps } as AcknowledgeIncidentProps),
    options
  );
}
