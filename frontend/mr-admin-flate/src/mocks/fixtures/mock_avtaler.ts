import { Avtale, Avtalestatus, Avtaletype, Opphav } from "mulighetsrommet-api-client";
import { mockEnheter } from "./mock_enheter";
import { mockTiltakstyper } from "./mock_tiltakstyper";
import { mockVirksomheter } from "./mock_virksomheter";

export const mockAvtaler: Avtale[] = [
  {
    id: "d1f163b7-1a41-4547-af16-03fd4492b7ba",
    tiltakstype: mockTiltakstyper.AVKLARAG,
    navn: "Testtiltak Varig",
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
    opphav: Opphav.MR_ADMIN_FLATE,
    avtalenummer: "2021#10579",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    leverandorKontaktperson: {
      navn: "Ole Kjetil Martinsen",
      id: "1234",
      epost: "ole.kjetil.martinsen@arrangor.no",
      telefon: "90123456",
      organisasjonsnummer: "123456789",
      beskrivelse: "Direktør",
    },
    startDato: "2021-08-02",
    sluttDato: "2026-08-01",
    navEnheter: [mockEnheter._0425, mockEnheter._0415],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.FORHAANDSGODKJENT,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: `Nye priser fra 21.03.23, gamle priser i parentes

        10 deltakere:
        Teori en uke: 31 239,- (30 329,-)                     Praksis en uke: 26 018,- (25 260,-)                      Kombinasjon en uke: 28 396,- (27 569,-)

        15 deltakere:
        Teori en uke: 40 549,- (39 368,-)                    Praksis en uke: 36 855,- (35 782,-)                      Kombinasjon en uke: 33 780,- (32 796,-)

        20 deltakere:
        Teori en uke: 56 771,- (55 117,-)                     Praksis en uke: 45 695,- (44 364,-)                       Kombinasjon en uke: 47 344,- (45 965,-)`,
    url: "https://www.mercell.no",
  },
  {
    id: "d1f163b7-1a41-4547-af16-03fd4492b7bc",
    tiltakstype: mockTiltakstyper.ARBFORB,
    navn: "Avtale hos ÅMLI KOMMUNE SAMFUNNSAVDELINGA",
    avtalenummer: "2021#10579",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2021-08-02",
    sluttDato: "2026-08-01",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
    url: null,
  },
  {
    id: "6374b285-989d-4f78-a59e-29481b64ba92",
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Åna Fengsel",
    avtalenummer: "2020#4929",
    url: "https://www.websak.no",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2020-07-01",
    sluttDato: "2024-06-30",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "1eea449a-2629-4abc-b151-76ebbd028401",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Arbeids- og sosialdepartementet",
    avtalenummer: "2020#4993",
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "P123456",
      navn: "Pelle Pioner",
    },
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2020-07-01",
    sluttDato: "2023-06-30",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
    url: "https://www.nav.no",
  },
  {
    id: "8a4a4dee-98c7-4a07-bc0c-f677a46c406f",
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "P123456",
      navn: "Pelle Pioner",
    },
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Arendal fengsel",
    avtalenummer: "2020#6480",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2020-07-01",
    sluttDato: "2024-06-30",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "6b33bdbf-621d-4e4a-aa35-3dea18f02327",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos ASKØY KOMMUNE BARNEHAGEADMINISTRASJ",
    avtalenummer: "2021#13076",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "P123456",
      navn: "Pelle Pioner",
    },
    startDato: "2021-11-01",
    sluttDato: "2023-10-31",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "bd429f41-0222-4374-82af-ccf932e2348d",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "P123456",
      navn: "Pelle Pioner",
    },
    navn: "Avtale hos ASKØY KOMMUNE KOMM TEKNISK AVD",
    avtalenummer: "2021#17845",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2021-11-01",
    sluttDato: "2024-06-30",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "e26c53cd-cafd-4a57-b7e0-c774cf33ea0d",
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "P123456",
      navn: "Pelle Pioner",
    },
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos AUST-AGDER SIVILFORSVARSDISTRIKT",
    avtalenummer: "2019#6552",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2019-08-20",
    sluttDato: "2023-08-19",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "6267aed1-1d7a-419a-83aa-1c42488b6bf1",
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Bærum kommune Vei Og Trafikk",
    avtalenummer: "2021#20070",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2022-01-01",
    sluttDato: "2025-05-09",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "dd85e265-30ad-4474-b3e5-7f5670b978c3",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos BUSINESS REGION KRISTIANSAND",
    avtalenummer: "2021#14456",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    opphav: Opphav.ARENA,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2021-10-01",
    sluttDato: "2024-12-31",
    navEnheter: [mockEnheter._0313, mockEnheter._0315, mockEnheter._0330],
    navRegion: mockEnheter._0300,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
  {
    id: "bc5fb428-e712-460c-9153-9e2f6caadf82",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos BYBANEN - BYBANEKONTORET",
    avtalenummer: "2020#6620",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2020-07-01",
    sluttDato: "2023-06-30",
    navEnheter: [mockEnheter._0313, mockEnheter._0315, mockEnheter._0330],
    navRegion: mockEnheter._0300,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AVBRUTT,
    prisbetingelser: "Maskert prisbetingelser",
    opphav: Opphav.MR_ADMIN_FLATE,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
  },
  {
    id: "f80817a4-4602-46e4-ae62-46ad28b023be",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Bydelsadm Bydel Grünerløkka",
    avtalenummer: "2019#2191",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2019-07-01",
    sluttDato: "2023-06-30",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
    opphav: Opphav.MR_ADMIN_FLATE,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
  },
  {
    id: "03d8e390-6c4d-4d8c-b42d-d9f39406086b",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos DRAMMENSREGIONENS BRANNVESEN IKS",
    avtalenummer: "2020#12202",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2020-10-01",
    sluttDato: "2023-09-30",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
    opphav: Opphav.MR_ADMIN_FLATE,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
  },
  {
    id: "21ab610d-5ed9-4d08-bdba-0650e2a56601",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos HÅ KOMMUNE SENTRALADMINISTRASJON",
    avtalenummer: "2021#18282",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2021-09-21",
    sluttDato: "2022-09-20",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AVSLUTTET,
    prisbetingelser: "Maskert prisbetingelser",
    opphav: Opphav.MR_ADMIN_FLATE,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
  },
  {
    id: "f671039e-796b-456f-a4b3-c95172e2142c",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Hardanger likningskontor",
    avtalenummer: "2021#20250",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2050-01-01",
    sluttDato: "2051-12-31",
    navEnheter: [],
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.PLANLAGT,
    prisbetingelser: "Maskert prisbetingelser",
    opphav: Opphav.MR_ADMIN_FLATE,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
  },
  {
    id: "8179667d-4cdb-4307-ac34-d459bda709a3",
    tiltakstype: mockTiltakstyper.INDOPPFAG,
    navn: "Avtale hos Hardanger likningskontor",
    avtalenummer: "2021#20172",
    leverandor: {
      ...mockVirksomheter.fretex,
      slettet: false,
    },
    leverandorUnderenheter: mockVirksomheter.fretex.underenheter!!.map((v) => ({
      organisasjonsnummer: v.organisasjonsnummer,
      navn: v.navn,
    })),
    startDato: "2022-01-01",
    sluttDato: "2024-12-31",
    navEnheter: [],
    opphav: Opphav.MR_ADMIN_FLATE,
    administrator: {
      navIdent: "B123456",
      navn: "Bertil Betabruker",
    },
    navRegion: mockEnheter._0400,
    avtaletype: Avtaletype.RAMMEAVTALE,
    avtalestatus: Avtalestatus.AKTIV,
    prisbetingelser: "Maskert prisbetingelser",
  },
];
