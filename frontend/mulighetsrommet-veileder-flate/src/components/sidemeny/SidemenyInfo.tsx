import { BodyShort, Box } from "@navikt/ds-react";
import {
  TiltaksgjennomforingOppstartstype,
  TiltakskodeArena,
  VeilederflateInnsatsgruppe,
  VeilederflateTiltak,
} from "@mr/api-client";
import { formaterDato, utledLopenummerFraTiltaksnummer } from "@/utils/Utils";
import Kopiknapp from "../kopiknapp/Kopiknapp";
import Regelverksinfo from "./Regelverksinfo";
import styles from "./SidemenyInfo.module.scss";
import { isTiltakGruppe } from "@/api/queries/useArbeidsmarkedstiltakById";

interface Props {
  innsatsgrupper: VeilederflateInnsatsgruppe[];
  tiltak: VeilederflateTiltak;
}

const SidemenyInfo = ({ innsatsgrupper, tiltak }: Props) => {
  const { tiltaksnummer, tiltakstype, stedForGjennomforing } = tiltak;

  const minimumInnsatsgruppe = innsatsgrupper
    .filter((innsatsgruppe) => (tiltakstype.innsatsgrupper ?? []).includes(innsatsgruppe.nokkel))
    .reduce((prev, current) => (prev.order < current.order ? prev : current));

  const arrangor = isTiltakGruppe(tiltak) ? tiltak.arrangor : null;

  return (
    <Box padding="5" background="bg-subtle" className={styles.panel} id="sidemeny">
      {tiltaksnummer && (
        <div className={styles.rad}>
          <BodyShort size="small" className={styles.tittel}>
            Tiltaksnummer
          </BodyShort>
          <div className={styles.tiltaksnummer}>
            <BodyShort size="small">{utledLopenummerFraTiltaksnummer(tiltaksnummer)}</BodyShort>
            <Kopiknapp
              kopitekst={utledLopenummerFraTiltaksnummer(tiltaksnummer)}
              dataTestId="knapp_kopier"
            />
          </div>
        </div>
      )}

      {stedForGjennomforing && (
        <div className={styles.rad}>
          <BodyShort size="small" className={styles.tittel}>
            Sted for gjennomføring
          </BodyShort>
          <BodyShort size="small">{stedForGjennomforing}</BodyShort>
        </div>
      )}

      <div className={styles.rad}>
        <BodyShort size="small" className={styles.tittel}>
          Tiltakstype
        </BodyShort>
        <BodyShort size="small">{tiltakstype.navn} </BodyShort>
      </div>

      {arrangor && (
        <div className={styles.rad}>
          <BodyShort size="small" className={styles.tittel}>
            Arrangør
          </BodyShort>
          <BodyShort size="small">{arrangor.selskapsnavn}</BodyShort>
        </div>
      )}

      <div className={styles.rad}>
        <BodyShort title="Minimum krav innsatsgruppe" size="small" className={styles.tittel}>
          <abbr title="Minimum">Min</abbr>. innsatsgruppe
        </BodyShort>
        <BodyShort size="small">{minimumInnsatsgruppe.tittel}</BodyShort>
      </div>

      <TiltakVarighetInfo tiltak={tiltak} />

      {tiltakstype.regelverkLenker && (
        <div className={styles.rad}>
          <BodyShort size="small" className={styles.tittel}>
            Regelverk og rutiner
          </BodyShort>
          <Regelverksinfo
            regelverkLenker={[
              ...tiltakstype.regelverkLenker,
              {
                _id: "klage",
                regelverkLenkeNavn: "Avslag og klage",
                regelverkUrl:
                  "https://navno.sharepoint.com/sites/fag-og-ytelser-arbeid-tiltak-og-virkemidler/SitePages/Klage-p%C3%A5-arbeidsmarkedstiltak.aspx",
              },
              {
                _id: "vurdering",
                regelverkLenkeNavn: "Tiltak hos familie/nærstående",
                regelverkUrl:
                  "https://navno.sharepoint.com/sites/fag-og-ytelser-arbeid-tiltak-og-virkemidler/SitePages/Rutine.aspx",
              },
            ]}
          />
        </div>
      )}
    </Box>
  );
};

function TiltakVarighetInfo({ tiltak }: { tiltak: VeilederflateTiltak }) {
  const visSluttdato =
    isTiltakGruppe(tiltak) &&
    tiltak.sluttdato &&
    tiltak.tiltakstype.arenakode &&
    [
      TiltakskodeArena.GRUPPEAMO,
      TiltakskodeArena.JOBBK,
      TiltakskodeArena.DIGIOPPARB,
      TiltakskodeArena.GRUFAGYRKE,
      TiltakskodeArena.ENKFAGYRKE,
    ].includes(tiltak.tiltakstype.arenakode);

  const tittel = visSluttdato ? "Varighet" : "Oppstart";

  const innhold = visSluttdato
    ? `${formaterDato(tiltak.oppstartsdato!)} - ${formaterDato(tiltak.sluttdato!)}`
    : tiltak.oppstart === TiltaksgjennomforingOppstartstype.FELLES
      ? formaterDato(tiltak.oppstartsdato)
      : "Løpende";

  return (
    <div className={styles.rad}>
      <BodyShort size="small" className={styles.tittel}>
        {tittel}
      </BodyShort>
      <BodyShort size="small">{innhold}</BodyShort>
    </div>
  );
}

export default SidemenyInfo;
