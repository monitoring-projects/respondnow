/* eslint-disable */
import { useMutation, UseMutationOptions } from '@tanstack/react-query';
import { fetcher, FetcherOptions } from '@services/fetcher';

export interface DeleteIncidentQueryParams {
  accountIdentifier: string;
}

export interface DeleteIncidentOkResponse {
  status: string;
  message: string;
  data?: {
    incidentId: string;
    deleted: boolean;
    correlationID?: string;
  };
}

export type DeleteIncidentErrorResponse = DeleteIncidentOkResponse;

export interface DeleteIncidentProps extends Omit<FetcherOptions<DeleteIncidentQueryParams, unknown>, 'url'> {
  incidentIdentifier: string;
  queryParams: DeleteIncidentQueryParams;
}

export function deleteIncident(props: DeleteIncidentProps): Promise<DeleteIncidentOkResponse> {
  const { incidentIdentifier, ...rest } = props;
  return fetcher<DeleteIncidentOkResponse, DeleteIncidentQueryParams, unknown>({
    url: `/api/incident/${incidentIdentifier}`,
    method: 'DELETE',
    ...rest
  });
}

export type DeleteIncidentMutationProps<T extends keyof DeleteIncidentProps> = Omit<DeleteIncidentProps, T> &
  Partial<Pick<DeleteIncidentProps, T>>;

/**
 * Delete an incident (soft delete)
 */
export function useDeleteIncidentMutation<T extends keyof DeleteIncidentProps>(
  props: Pick<Partial<DeleteIncidentProps>, T>,
  options?: Omit<
    UseMutationOptions<DeleteIncidentOkResponse, DeleteIncidentErrorResponse, DeleteIncidentMutationProps<T>>,
    'mutationKey' | 'mutationFn'
  >
) {
  return useMutation<DeleteIncidentOkResponse, DeleteIncidentErrorResponse, DeleteIncidentMutationProps<T>>(
    (mutateProps: DeleteIncidentMutationProps<T>) => deleteIncident({ ...props, ...mutateProps } as DeleteIncidentProps),
    options
  );
}
