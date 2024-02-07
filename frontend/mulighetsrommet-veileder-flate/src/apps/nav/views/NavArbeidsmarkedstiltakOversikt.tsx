import { TiltakLoader } from "@/components/TiltakLoader";
import { Feilmelding } from "@/components/feilmelding/Feilmelding";
import { FilterAndTableLayout } from "@/components/filtrering/FilterAndTableLayout";
import { Tiltaksgjennomforingsoversikt } from "@/components/oversikt/Tiltaksgjennomforingsoversikt";
import { useNavTiltaksgjennomforinger } from "@/core/api/queries/useTiltaksgjennomforinger";
import { FilterMenyMedSkeletonLoader } from "@/components/filtrering/FilterMenyMedSkeletonLoader";
import { Button } from "@navikt/ds-react";
import {
  isFilterReady,
  useResetArbeidsmarkedstiltakFilterUtenBrukerIKontekst,
} from "@/hooks/useArbeidsmarkedstiltakFilter";
import { NavFilterTags } from "@/apps/nav/filtrering/NavFilterTags";

interface Props {
  preview?: boolean;
}

export const NavArbeidsmarkedstiltakOversikt = ({ preview = false }: Props) => {
  const { data: tiltaksgjennomforinger = [], isLoading } = useNavTiltaksgjennomforinger({
    preview,
  });

  const { filter, filterHasChanged, resetFilterToDefaults } =
    useResetArbeidsmarkedstiltakFilterUtenBrukerIKontekst();

  return (
    <FilterAndTableLayout
      buttons={null}
      filter={<FilterMenyMedSkeletonLoader />}
      tags={<NavFilterTags />}
      resetButton={
        filterHasChanged && (
          <Button
            size="small"
            variant="tertiary"
            onClick={resetFilterToDefaults}
            data-testid="knapp_nullstill-filter"
          >
            Nullstill filter
          </Button>
        )
      }
      table={
        <div>
          {isLoading ? (
            <TiltakLoader />
          ) : !isFilterReady(filter) ? (
            <Feilmelding
              data-testid="filter-mangler-verdier-feilmelding"
              header="Du må filtrere på en innsatsgruppe og minst én NAV-enhet for å se tiltaksgjennomføringer"
              ikonvariant="info"
            />
          ) : tiltaksgjennomforinger.length === 0 ? (
            <Feilmelding
              header="Ingen tiltaksgjennomføringer funnet"
              beskrivelse="Prøv å justere søket eller filteret for å finne det du leter etter"
              ikonvariant="warning"
            />
          ) : (
            <Tiltaksgjennomforingsoversikt tiltaksgjennomforinger={tiltaksgjennomforinger} />
          )}
        </div>
      }
    />
  );
};
