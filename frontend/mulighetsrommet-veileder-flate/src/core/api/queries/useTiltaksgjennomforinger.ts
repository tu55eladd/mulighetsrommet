import { useQuery } from "@tanstack/react-query";
import { mulighetsrommetClient } from "@/core/api/clients";
import { QueryKeys } from "@/core/api/query-keys";
import {
  useArbeidsmarkedstiltakFilterValue,
  valgteEnhetsnumre,
} from "@/hooks/useArbeidsmarkedstiltakFilter";

export function useVeilederTiltaksgjennomforinger() {
  const { queryIsValid, query } = useGetArbeidsmarkedstiltakFilterAsQuery();

  return useQuery({
    queryKey: QueryKeys.sanity.tiltaksgjennomforinger(query),
    queryFn: () => mulighetsrommetClient.veilederTiltak.getVeilederTiltaksgjennomforinger(query),
    enabled: queryIsValid,
  });
}

export function useNavTiltaksgjennomforinger() {
  const { queryIsValid, query } = useGetArbeidsmarkedstiltakFilterAsQuery();

  return useQuery({
    queryKey: QueryKeys.sanity.tiltaksgjennomforinger(query),
    queryFn: () => mulighetsrommetClient.veilederTiltak.getNavTiltaksgjennomforinger(query),
    enabled: queryIsValid,
  });
}

export function usePreviewTiltaksgjennomforinger() {
  const { queryIsValid, query } = useGetArbeidsmarkedstiltakFilterAsQuery();

  return useQuery({
    queryKey: QueryKeys.sanity.tiltaksgjennomforingerPreview(query),
    queryFn: () => mulighetsrommetClient.veilederTiltak.getPreviewTiltaksgjennomforinger(query),
    enabled: queryIsValid,
  });
}

function useGetArbeidsmarkedstiltakFilterAsQuery() {
  const filter = useArbeidsmarkedstiltakFilterValue();

  const tiltakstyper =
    filter.tiltakstyper.length !== 0 ? filter.tiltakstyper.map(({ id }) => id) : undefined;

  const enheter = valgteEnhetsnumre(filter);

  return {
    queryIsValid: enheter.length !== 0 && filter.innsatsgruppe !== undefined,
    query: {
      search: filter.search || undefined,
      apentForInnsok: filter.apentForInnsok,
      innsatsgruppe: filter.innsatsgruppe?.nokkel,
      enheter,
      tiltakstyper,
    },
  };
}
