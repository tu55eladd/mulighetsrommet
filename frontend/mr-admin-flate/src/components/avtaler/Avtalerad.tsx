import { BodyShort } from "@navikt/ds-react";
import { Avtale } from "mulighetsrommet-api-client";
import { capitalizeEveryWord, formaterDato } from "../../utils/Utils";
import styles from "../listeelementer/Listeelementer.module.scss";
import { ListeRad } from "../listeelementer/ListeRad";
import { Avtalestatus } from "../statuselementer/Avtalestatus";

interface Props {
  avtale: Avtale;
}

export function Avtalerad({ avtale }: Props) {
  return (
    <ListeRad
      linkTo={`/avtaler/${avtale.id}`}
      classname={styles.listerad_avtale}
    >
      <BodyShort size="medium">{avtale.navn}</BodyShort>
      <BodyShort size="medium">
        {capitalizeEveryWord(avtale.leverandor?.navn, ["og"]) || ""}
      </BodyShort>
      <BodyShort size="medium">
        {avtale.navEnhet?.navn || avtale?.navEnhet?.enhetsnummer}
      </BodyShort>

      <BodyShort
        size="small"
        title={`Startdato ${formaterDato(avtale.startDato)}`}
      >
        {formaterDato(avtale.startDato)}
      </BodyShort>
      <BodyShort
        size="small"
        title={`Sluttdato ${formaterDato(avtale.sluttDato)}`}
      >
        {formaterDato(avtale.sluttDato)}
      </BodyShort>
      <BodyShort size="small">
        <Avtalestatus avtale={avtale} />
      </BodyShort>
    </ListeRad>
  );
}
