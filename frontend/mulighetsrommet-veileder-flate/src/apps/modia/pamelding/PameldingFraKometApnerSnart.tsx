import { VeilederflateTiltaksgjennomforing } from "@mr/api-client";
import { ExternalLinkIcon } from "@navikt/aksel-icons";
import { BodyShort, GuidePanel, Heading, Link, VStack } from "@navikt/ds-react";
import { useTiltakstyperSomSnartStotterPameldingIModia } from "../../../api/queries/useTiltakstyperSomStotterPameldingIModia";
import { TegnestiftIkon } from "../../../ikoner/TegnestiftIkon";
import styles from "./PameldingFraKometApnerSnart.module.scss";

interface Props {
  tiltaksgjennomforing: VeilederflateTiltaksgjennomforing;
}

export function PameldingFraKometApnerSnart({ tiltaksgjennomforing }: Props) {
  const { data } = useTiltakstyperSomSnartStotterPameldingIModia();

  if (
    tiltaksgjennomforing?.tiltakstype?.tiltakskode &&
    !data?.includes(tiltaksgjennomforing.tiltakstype.tiltakskode)
  ) {
    return null;
  }

  return (
    <GuidePanel className={styles.overwritten_poster} illustration={<TegnestiftIkon />}>
      <VStack gap="3">
        <Heading level="4" size="small" className={styles.text_center}>
          Ny påmeldingsløsning i Modia kommer snart
        </Heading>
        <BodyShort>
          Påmelding på arbeidsmarkedstiltak skal i fremtiden gjøres her i Modia. AFT-tiltaket er den
          første tiltakstypen i ny løsning.
        </BodyShort>
        <BodyShort>
          <Link
            target="_blank"
            rel="noreferrer noopener"
            href="https://navno.sharepoint.com/sites/intranett-produktomrader-og-prosjekter/SitePages/Ny-l%C3%B8sning-for-p%C3%A5melding-til-arbeidsforberedende-trening.aspx"
          >
            Les mer på navet her <ExternalLinkIcon title="Ikon for å åpne lenke i ny fane" />
          </Link>
        </BodyShort>
      </VStack>
    </GuidePanel>
  );
}