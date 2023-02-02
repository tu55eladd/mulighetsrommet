import { Heading, Search, Select } from "@navikt/ds-react";
import { useAtom } from "jotai";
import { ChangeEvent } from "react";
import { tiltakstypefilter, paginationAtom } from "../../api/atoms";
import { TiltakstyperOversikt } from "../../components/tiltakstyper/TiltakstyperOversikt";
import styles from "../Page.module.scss";

export function TiltakstyperPage() {
  const [sokefilter, setSokefilter] = useAtom(tiltakstypefilter);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [_, setPage] = useAtom(paginationAtom);

  const resetPagination = () => {
    setPage(1);
  };

  return (
    <>
      <div className={styles.header_wrapper}>
        <Heading size="large">Oversikt over tiltakstyper</Heading>
      </div>
      <div className={styles.filterseksjon}>
        <Search
          label="Søk etter tiltakstype"
          hideLabel={false}
          variant="simple"
          onChange={(sok: string) => setSokefilter({ ...sokefilter, sok })}
          value={sokefilter.sok}
          aria-label="Søk etter tiltakstype"
          data-testid="filter_sokefelt"
          size="small"
        />
        <Select
          label="Filtrer på statuser"
          size="small"
          value={sokefilter.status}
          onChange={(e: ChangeEvent<HTMLSelectElement>) => {
            resetPagination();
            setSokefilter({
              ...sokefilter,
              status: e.currentTarget.value as any,
            });
          }}
        >
          <option value="AKTIV">Aktive</option>
          <option value="PLANLAGT">Planlagte</option>
          <option value="UTFASET">Utfasede</option>
        </Select>
        <Select
          label="Gruppetiltak eller individuelle tiltak"
          size="small"
          value={sokefilter.kategori}
          onChange={(e: ChangeEvent<HTMLSelectElement>) => {
            resetPagination();
            const kategori = e.currentTarget.value as any;
            setSokefilter({
              ...sokefilter,
              kategori: kategori === "ALLE" ? undefined : kategori,
            });
          }}
        >
          <option value="GRUPPE">Gruppetiltak</option>
          <option value="INDIVIDUELL">Individuelle tiltak</option>
          <option value="ALLE">Alle</option>
        </Select>
      </div>
      <TiltakstyperOversikt />
    </>
  );
}
