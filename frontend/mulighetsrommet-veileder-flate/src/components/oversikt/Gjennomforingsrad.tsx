import classNames from "classnames";
import { useAtom } from "jotai";
import {
  TiltaksgjennomforingOppstartstype,
  VeilederflateTiltaksgjennomforing,
} from "mulighetsrommet-api-client";
import { paginationAtom } from "../../core/atoms/atoms";
import { erPreview, formaterDato, kebabCase } from "../../utils/Utils";
import Lenke from "../lenke/Lenke";
import styles from "./Gjennomforingsrad.module.scss";
import { BodyShort } from "@navikt/ds-react";
import { ChevronRightIcon, PadlockLockedFillIcon } from "@navikt/aksel-icons";

interface Props {
  tiltaksgjennomforing: VeilederflateTiltaksgjennomforing;
  index: number;
}

const visOppstartsdato = (oppstart: TiltaksgjennomforingOppstartstype, oppstartsdato?: string) => {
  switch (oppstart) {
    case TiltaksgjennomforingOppstartstype.FELLES:
      return formaterDato(oppstartsdato!);
    case TiltaksgjennomforingOppstartstype.LOPENDE:
      return "Løpende oppstart";
  }
};

export function Gjennomforingsrad({ tiltaksgjennomforing, index }: Props) {
  const [page] = useAtom(paginationAtom);
  const { id, sanityId, navn, arrangor, tiltakstype, oppstart, oppstartsdato, apentForInnsok } =
    tiltaksgjennomforing;

  return (
    <li
      className={styles.list_element}
      id={`list_element_${index}`}
      data-testid={`tiltaksgjennomforing_${kebabCase(navn)}`}
    >
      <Lenke
        to={
          erPreview()
            ? `/preview/${id ?? sanityId}#page=${page}`
            : `/arbeidsmarkedstiltak/tiltak/${id ?? sanityId}#page=${page}`
        }
      >
        <div className={styles.gjennomforing_container}>
          {!apentForInnsok && (
            <PadlockLockedFillIcon
              className={styles.status}
              title="Tiltaket er stengt for innsøking"
            />
          )}

          <div className={classNames(styles.flex, styles.navn)}>
            <BodyShort
              size="small"
              title={navn}
              className={classNames(styles.truncate, styles.as_link)}
            >
              {navn}
            </BodyShort>
            <BodyShort size="small" title={arrangor?.selskapsnavn} className={styles.muted}>
              {arrangor?.selskapsnavn}
            </BodyShort>
          </div>

          <div className={classNames(styles.infogrid, styles.metadata)}>
            <BodyShort size="small" title={tiltakstype.navn}>
              {tiltakstype.navn}
            </BodyShort>
            <BodyShort
              size="small"
              title={visOppstartsdato(oppstart, oppstartsdato)}
              className={styles.truncate}
            >
              {visOppstartsdato(oppstart, oppstartsdato)}
            </BodyShort>
          </div>

          <ChevronRightIcon className={styles.ikon} title="Detaljer om tiltaket" />
        </div>
      </Lenke>
    </li>
  );
}
