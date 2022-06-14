import { useQuery } from 'react-query';
import { QueryKeys } from '../../core/api/QueryKeys';
import { MulighetsrommetService, SanityResponse } from 'mulighetsrommet-api-client';

export function useSanity<T>(query: string) {
  return useQuery(
    [QueryKeys.SanityQuery, query],
    () => MulighetsrommetService.sanityQuery({ query }) as Promise<SanityResponse>
  );
}
