import { ExternalLinkIcon } from "@navikt/aksel-icons";
import { Alert, Heading, HelpText, VStack } from "@navikt/ds-react";
import { NOM_ANSATT_SIDE } from "mulighetsrommet-frontend-common/constants";
import { Fragment } from "react";
import { useAvtale } from "../../api/avtaler/useAvtale";
import { Bolk } from "../../components/detaljside/Bolk";
import { Metadata, Separator } from "../../components/detaljside/Metadata";
import { Laster } from "../../components/laster/Laster";
import { avtaletypeTilTekst, formaterDato } from "../../utils/Utils";
import { erAnskaffetTiltak } from "../../utils/tiltakskoder";
import styles from "../DetaljerInfo.module.scss";
import { Link } from "react-router-dom";
import { NavEnhet } from "mulighetsrommet-api-client";
import { avtaletekster } from "../../components/ledetekster/avtaleLedetekster";

export function AvtaleDetaljer() {
  const { data: avtale, isPending, error } = useAvtale();

  if (isPending) {
    return <Laster tekst="Laster avtale..." />;
  }

  if (error) {
    return <Alert variant="error">Klarte ikke hente avtaleinformasjon</Alert>;
  }

  const lenketekst = () => {
    let tekst;
    if (avtale?.url?.includes("websak")) {
      tekst = `Se originalavtale i WebSak `;
    } else {
      tekst = `Se originalavtale `;
    }
    return (
      <>
        {tekst}
        <ExternalLinkIcon aria-label="Ekstern lenke" />
      </>
    );
  };

  function sorterPaRegionsnavn(a: { region: NavEnhet }, b: { region: NavEnhet }) {
    return a.region.navn.localeCompare(b.region.navn);
  }

  const {
    navn,
    avtalenummer,
    tiltakstype,
    avtaletype,
    startDato,
    sluttDato,
    administratorer,
    url,
    kontorstruktur,
    arenaAnsvarligEnhet,
    leverandor,
  } = avtale;

  return (
    <div className={styles.container}>
      <div className={styles.detaljer}>
        <Bolk aria-label="Avtalenavn og avtalenummer">
          <Metadata header={avtaletekster.avtalenavnLabel} verdi={navn} />
          <Metadata header={avtaletekster.avtalenummerLabel} verdi={avtalenummer} />
        </Bolk>

        <Bolk aria-label={avtaletekster.tiltakstypeLabel}>
          <Metadata
            header={avtaletekster.tiltakstypeLabel}
            verdi={<Link to={`/tiltakstyper/${tiltakstype.id}`}>{tiltakstype.navn}</Link>}
          />
          <Metadata header={avtaletekster.avtaletypeLabel} verdi={avtaletypeTilTekst(avtaletype)} />
        </Bolk>

        <Separator />

        <Heading size="small" as="h3">
          Avtalens varighet
        </Heading>

        <Bolk aria-label="Start- og sluttdato">
          <Metadata header={avtaletekster.startdatoLabel} verdi={formaterDato(startDato)} />
          <Metadata
            header={avtaletekster.sluttdatoLabel}
            verdi={sluttDato ? formaterDato(sluttDato) : "-"}
          />
        </Bolk>

        <Separator />

        <VStack gap="5">
          <Bolk aria-label={avtaletekster.prisOgBetalingLabel}>
            {erAnskaffetTiltak(tiltakstype.arenaKode) && (
              <Metadata
                header={avtaletekster.prisOgBetalingLabel}
                verdi={
                  avtale.prisbetingelser ??
                  "Det eksisterer ikke pris og betalingsbetingelser for denne avtalen"
                }
              />
            )}
          </Bolk>

          {administratorer ? (
            <Bolk aria-label={avtaletekster.administratorerForAvtalenLabel}>
              <Metadata
                header={avtaletekster.administratorerForAvtalenLabel}
                verdi={
                  administratorer.length ? (
                    <ul>
                      {administratorer.map((admin) => {
                        return (
                          <li key={admin.navIdent}>
                            <a
                              target="_blank"
                              rel="noopener noreferrer"
                              href={`${NOM_ANSATT_SIDE}${admin.navIdent}`}
                            >
                              {`${admin.navn} - ${admin.navIdent}`}{" "}
                              <ExternalLinkIcon aria-label="Ekstern lenke" />
                            </a>
                          </li>
                        );
                      })}
                    </ul>
                  ) : (
                    avtaletekster.ingenAdministratorerSattLabel
                  )
                }
              />
              {url ? (
                <Metadata
                  header={avtaletekster.seOriginalavtaleLabel}
                  verdi={
                    <Link
                      className={styles.websakLenke}
                      to={url}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {lenketekst()}
                    </Link>
                  }
                />
              ) : null}
            </Bolk>
          ) : null}
        </VStack>
      </div>

      <div className={styles.detaljer}>
        {kontorstruktur.length > 1 ? (
          <Metadata
            header={avtaletekster.fylkessamarbeidLabel}
            verdi={
              <ul>
                {kontorstruktur.sort(sorterPaRegionsnavn).map((kontor) => {
                  return <li key={kontor.region.enhetsnummer}>{kontor.region.navn}</li>;
                })}
              </ul>
            }
          />
        ) : (
          kontorstruktur.map((struktur, index) => {
            return (
              <Fragment key={index}>
                <Bolk aria-label={avtaletekster.fylkessamarbeidLabel}>
                  <Metadata
                    header={avtaletekster.fylkessamarbeidLabel}
                    verdi={struktur.region.navn}
                  />
                </Bolk>

                <Bolk aria-label={avtaletekster.navEnheterLabel}>
                  <Metadata
                    header={avtaletekster.navEnheterLabel}
                    verdi={
                      <ul>
                        {struktur.kontorer.map((kontor) => (
                          <li key={kontor.enhetsnummer}>{kontor.navn}</li>
                        ))}
                      </ul>
                    }
                  />
                </Bolk>
              </Fragment>
            );
          })
        )}
        {arenaAnsvarligEnhet ? (
          <div style={{ display: "flex", gap: "1rem", margin: "0.5rem 0" }}>
            <dl style={{ margin: "0" }}>
              <Metadata
                header={avtaletekster.ansvarligEnhetFraArenaLabel}
                verdi={`${arenaAnsvarligEnhet.enhetsnummer} ${arenaAnsvarligEnhet.navn}`}
              />
            </dl>
            <HelpText title="Hva betyr feltet 'Ansvarlig enhet fra Arena'?">
              Ansvarlig enhet fra Arena blir satt i Arena basert på tiltaksansvarlig sin enhet når
              det opprettes avtale i Arena.
            </HelpText>
          </div>
        ) : null}

        <Separator />

        <Bolk aria-label={avtaletekster.tiltaksarrangorHovedenhetLabel}>
          <Metadata
            header={avtaletekster.tiltaksarrangorHovedenhetLabel}
            verdi={[leverandor.navn, leverandor.organisasjonsnummer].filter(Boolean).join(" - ")}
          />
        </Bolk>

        <Bolk aria-label={avtaletekster.tiltaksarrangorUnderenheterLabel}>
          <Metadata
            header={avtaletekster.tiltaksarrangorUnderenheterLabel}
            verdi={
              <ul>
                {leverandor.underenheter.map((enhet) => (
                  <li key={enhet.organisasjonsnummer}>
                    {enhet?.navn
                      ? `${enhet.navn} - ${enhet.organisasjonsnummer}`
                      : `${enhet.organisasjonsnummer}`}
                  </li>
                ))}
              </ul>
            }
          />
        </Bolk>

        <Separator />

        {leverandor.kontaktperson ? (
          <Bolk aria-label={avtaletekster.kontaktpersonHosTiltaksarrangorLabel}>
            <Metadata
              header={avtaletekster.kontaktpersonHosTiltaksarrangorLabel}
              verdi={
                <div className={styles.leverandor_kontaktinfo}>
                  <label>{leverandor.kontaktperson.navn}</label>
                  {leverandor.kontaktperson.telefon && (
                    <label>{leverandor.kontaktperson.telefon}</label>
                  )}
                  <a href={`mailto:${leverandor.kontaktperson.epost}`}>
                    {leverandor.kontaktperson.epost}
                  </a>
                  {leverandor.kontaktperson.beskrivelse && (
                    <label>{leverandor.kontaktperson.beskrivelse}</label>
                  )}
                </div>
              }
            />
          </Bolk>
        ) : null}
      </div>
    </div>
  );
}
