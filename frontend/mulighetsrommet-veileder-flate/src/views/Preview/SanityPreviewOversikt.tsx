import { useAtom } from "jotai";
import Filtermeny from "../../components/filtrering/Filtermeny";
import { FiltrertFeilInnsatsgruppeVarsel } from "../../components/ikkeKvalifisertVarsel/FiltrertFeilInnsatsgruppeVarsel";
import Tiltaksgjennomforingsoversikt from "../../components/oversikt/Tiltaksgjennomforingsoversikt";
import { tiltaksgjennomforingsfilter } from "../../core/atoms/atoms";
import styles from "../tiltaksgjennomforing-oversikt/ViewTiltaksgjennomforingOversikt.module.scss";
import { Bruker, Innsatsgruppe, NavEnhet, NavEnhetType } from "mulighetsrommet-api-client";
import { usePreviewTiltaksgjennomforinger } from "../../core/api/queries/usePreviewTiltaksgjennomforinger";
import { Loader } from "@navikt/ds-react";
import { useNavEnheter } from "../../core/api/queries/useNavEnheter";
import { SokeSelect } from "mulighetsrommet-frontend-common";
import { useEffect, useState } from "react";
import { Separator } from "../../utils/Separator";

export const SanityPreviewOversikt = () => {
  const [filter] = useAtom(tiltaksgjennomforingsfilter);
  const [geografiskEnhet, setGeografiskEnhet] = useState<NavEnhet | undefined>();
  const brukerdata: Bruker = {
    fnr: "01010199999",
    innsatsgruppe: Innsatsgruppe.VARIG_TILPASSET_INNSATS,
    oppfolgingsenhet: { navn: "", enhetId: "" },
    geografiskEnhet: { navn: "", enhetsnummer: "" },
  };
  const {
    data: tiltaksgjennomforinger = [],
    isFetching,
    refetch,
  } = usePreviewTiltaksgjennomforinger(geografiskEnhet?.enhetsnummer);
  const { data: enheter } = useNavEnheter();

  useEffect(() => {
    refetch();
  }, [geografiskEnhet]);

  if (!enheter || !tiltaksgjennomforinger) {
    return <Loader />;
  }

  return (
    <>
      <SokeSelect
        label="Geografisk enhet (denne er kun til preview funksjonalitet)"
        value={
          geografiskEnhet !== undefined
            ? { label: geografiskEnhet.navn, value: geografiskEnhet.enhetsnummer }
            : null
        }
        name="geografisk-enhet"
        size="medium"
        onChange={(e) =>
          setGeografiskEnhet(enheter.find((enhet) => enhet.enhetsnummer === e.target.value))
        }
        placeholder="Velg brukers geografiske enhet"
        options={enheter
          .filter((enhet) => enhet.type === NavEnhetType.LOKAL)
          .map((enhet) => ({
            label: enhet.navn,
            value: enhet.enhetsnummer,
          }))}
      />
      <Separator />

      <div className={styles.tiltakstype_oversikt} data-testid="tiltakstype-oversikt">
        <Filtermeny />
        <div>
          <FiltrertFeilInnsatsgruppeVarsel filter={filter} />
          <Tiltaksgjennomforingsoversikt
            tiltaksgjennomforinger={tiltaksgjennomforinger}
            isFetching={isFetching}
            brukerdata={brukerdata}
          />
        </div>
      </div>
    </>
  );
};
