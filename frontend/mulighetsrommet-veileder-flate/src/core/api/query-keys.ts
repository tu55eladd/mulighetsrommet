import { JoyrideType, NavEnhetStatus, NavEnhetType } from "mulighetsrommet-api-client";

export const QueryKeys = {
  SanityQuery: "sanityQuery",
  Brukerdata: "brukerdata",
  Veilederdata: "veilederdata",
  Historikk: "historikk",
  DeltMedBrukerStatus: "deltMedBrukerStatus",
  AlleDeltMedBrukerStatus: "alleDeltMedBrukerStatus",
  sanity: {
    innsatsgrupper: ["innsatsgrupper"],
    tiltakstyper: ["tiltakstyper"],
    tiltaksgjennomforinger: (tiltaksgjennomforingsfilter?: object) => [
      "tiltaksgjennomforinger",
      { ...tiltaksgjennomforingsfilter },
    ],
    tiltaksgjennomforing: (id: string, enheter: string[]) => ["tiltaksgjennomforing", id, enheter],
  },
  features: (feature: string) => [feature, "feature"],
  navEnheter: (statuser: NavEnhetStatus[], typer: NavEnhetType[]) => [
    statuser,
    typer,
    "navEnheter",
  ],
  navRegioner: ["navRegioner"],
  oppskrifter: (tiltakstypeId: string) => [tiltakstypeId, "oppskrifter"],
  harFullfortJoyride: (joyrideType: JoyrideType) => [joyrideType, "joyride"],
  overordnetEnhet: (enhetsnummer: string) => ["overordnetEnhet", enhetsnummer],
};
