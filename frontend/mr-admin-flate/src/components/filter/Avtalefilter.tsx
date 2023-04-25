import { Button, Search, Select } from "@navikt/ds-react";
import { useAtom } from "jotai";
import { Avtalestatus, Tiltakstypestatus } from "mulighetsrommet-api-client";
import { ChangeEvent, useEffect, useRef, useState } from "react";
import { avtaleFilter, avtalePaginationAtom } from "../../api/atoms";
import { useAvtaler } from "../../api/avtaler/useAvtaler";
import { useEnheter } from "../../api/enhet/useEnheter";
import { useAlleTiltakstyper } from "../../api/tiltakstyper/useAlleTiltakstyper";
import { resetPaginering } from "../../utils/Utils";
import styles from "./Filter.module.scss";
import OpprettAvtaleModal from "../avtaler/opprett/OpprettAvtaleModal";
import {
  OPPRETT_AVTALE_ADMIN_FLATE,
  useFeatureToggles,
} from "../../api/features/feature-toggles";

type Filters = "tiltakstype";

interface Props {
  skjulFilter?: Record<Filters, boolean>;
}

export function Avtalefilter(props: Props) {
  const [filter, setFilter] = useAtom(avtaleFilter);
  const { data: enheter } = useEnheter();
  const { data: tiltakstyper } = useAlleTiltakstyper({
    tiltakstypestatus: Tiltakstypestatus.AKTIV,
  });
  const { data } = useAvtaler();
  const [, setPage] = useAtom(avtalePaginationAtom);
  const searchRef = useRef<HTMLDivElement | null>(null);
  const [modalOpen, setModalOpen] = useState<boolean>(false);

  const features = useFeatureToggles();
  const visOpprettAvtaleknapp =
    features.isSuccess && features.data[OPPRETT_AVTALE_ADMIN_FLATE];

  useEffect(() => {
    // Hold fokus på søkefelt dersom bruker skriver i søkefelt
    if (filter.sok !== "") {
      searchRef?.current?.focus();
    }
  }, [data]);

  return (
    <>
      <div className={styles.filter_container}>
        <div className={styles.filter_left}>
          <Search
            ref={searchRef}
            label="Søk etter avtale"
            hideLabel
            size="small"
            variant="simple"
            onChange={(sok: string) => {
              setFilter({ ...filter, sok });
            }}
            value={filter.sok}
            aria-label="Søk etter avtale"
            data-testid="filter_avtale_sokefelt"
          />
          <Select
            label="Filtrer på statuser"
            hideLabel
            size="small"
            value={filter.status}
            data-testid="filter_avtale_status"
            onChange={(e: ChangeEvent<HTMLSelectElement>) => {
              resetPaginering(setPage);
              setFilter({
                ...filter,
                status: e.currentTarget.value as Avtalestatus,
              });
            }}
          >
            <option value="Aktiv">Aktiv</option>
            <option value="Planlagt">Planlagt</option>
            <option value="Avsluttet">Avsluttet</option>
            <option value="Avbrutt">Avbrutt</option>
            <option value="">Alle statuser</option>
          </Select>
          <Select
            label="Filtrer på enhet"
            hideLabel
            size="small"
            value={filter.enhet}
            data-testid="filter_avtale_enhet"
            onChange={(e: ChangeEvent<HTMLSelectElement>) => {
              resetPaginering(setPage);
              setFilter({ ...filter, enhet: e.currentTarget.value });
            }}
          >
            <option value="">Alle enheter</option>
            {enheter?.map((enhet) => (
              <option key={enhet.enhetId} value={enhet.enhetNr}>
                {enhet.navn} - {enhet.enhetNr}
              </option>
            ))}
          </Select>
          {props.skjulFilter?.tiltakstype ? null : (
            <Select
              label="Filtrer på tiltakstype"
              hideLabel
              size="small"
              value={filter.tiltakstype}
              data-testid="filter_avtale_tiltakstype"
              onChange={(e: ChangeEvent<HTMLSelectElement>) => {
                resetPaginering(setPage);
                setFilter({ ...filter, tiltakstype: e.currentTarget.value });
              }}
            >
              <option value="">Alle tiltakstyper</option>
              {tiltakstyper?.data?.map((tiltakstype) => (
                <option key={tiltakstype.id} value={tiltakstype.id}>
                  {tiltakstype.navn}
                </option>
              ))}
            </Select>
          )}
        </div>
        <div className={styles.filter_right}>
          {visOpprettAvtaleknapp && (
            <>
              <Button
                onClick={() => setModalOpen(true)}
                data-testid="registrer-ny-avtale"
                size="small"
              >
                Registrer avtale
              </Button>
              <OpprettAvtaleModal
                modalOpen={modalOpen}
                onClose={() => setModalOpen(false)}
              />
            </>
          )}
        </div>
      </div>
    </>
  );
}
