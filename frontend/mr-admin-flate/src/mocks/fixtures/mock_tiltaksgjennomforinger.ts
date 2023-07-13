import {
  Opphav,
  PaginertTiltaksgjennomforing,
  TiltaksgjennomforingStatus,
} from "mulighetsrommet-api-client";
import { Tilgjengelighetsstatus } from "mulighetsrommet-api-client/build/models/Tilgjengelighetsstatus";

export const mockTiltaksgjennomforinger: PaginertTiltaksgjennomforing = {
  pagination: {
    totalCount: 18,
    currentPage: 1,
    pageSize: 50,
  },
  data: [
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee361",
      navn: "Yrkesnorsk med praksis med en veldig lang tittel som ikke er helt utenkelig at de skriver inn",
      tiltaksnummer: "123456",
      antallPlasser: 50,
      arrangorOrganisasjonsnummer: "123456789",
      arrangorNavn: "Valp AS",
      avtaleId: "d1f163b7-1a41-4547-af16-03fd4492b7ba",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "Avklaring",
        arenaKode: "AVKLAR",
      },
      ansvarlig: {
        navident: "B123456",
        navn: "Bertil Betabruker",
      },
      sanityId: "1234",
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      stengtFra: "2023-6-1",
      stengtTil: "2023-8-1",
      arenaAnsvarligEnhet: "2990",
      navEnheter: [
        {
          enhetsnummer: "5701",
          navn: "NAV Falkenborg",
        },
        {
          enhetsnummer: "5703",
          navn: "NAV Indre Fosen",
        },
        {
          enhetsnummer: "0501",
          navn: "NAV Lillehammer-Gausdal",
        },
      ],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      lokasjonArrangor: "Brummundal",
      arrangorKontaktperson: {
        epost: "johnny.bravo@bravissimo.no",
        id: "123",
        navn: "Johnny Bravo",
        organisasjonsnummer: "123456789",
        telefon: "67543215",
        beskrivelse: null,
      },
      kontaktpersoner: [
        {
          epost: "per.richard.olsen@nav.no",
          mobilnummer: "90567894",
          navIdent: "O123456",
          navn: "Per Richard Olsen",
          navEnheter: ["5701"],
        },
        {
          epost: "nikoline.madsen@nav.no",
          mobilnummer: "90764321",
          navIdent: "M654378",
          navn: "Nikoline Madsen",
          navEnheter: ["5703", "0501"],
        },
        {
          epost: "petrus.pilsen@nav.no",
          mobilnummer: "78654323",
          navIdent: "M887654",
          navn: "Petrus Pilsen",
          navEnheter: [],
        },
      ],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee362",
      navn: "Spillbasert kvalifisering",
      tiltaksnummer: "123456",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "SoloPolo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "2990",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.AVLYST,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee363",
      navn: "Midlertidig lønnstilskudd",
      tiltaksnummer: "654434",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Solo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "2990",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee364",
      navn: "AFT - Unikom",
      tiltaksnummer: "768672",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Valp AS",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee365",
      navn: "Varig lønnstilskudd",
      tiltaksnummer: "65645",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Valp AS",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee366",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "32557",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Utvikler AS",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.AVBRUTT,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee367",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "98643",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Lady Grey",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.MR_ADMIN_FLATE,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee368",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "575685",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Lady Grey",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.AVLYST,
      opphav: Opphav.ARENA,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee369",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "54353",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Lady Grey",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee310",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "23213",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Lady Grey",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee311",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "76575",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Lady Grey",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee312",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "23123",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Lady Grey",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      kontaktpersoner: [],
      stengtFra: "2022-01-01",
      stengtTil: "2022-12-12",
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee313",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "686585",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "SoloPolo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      kontaktpersoner: [],
      stengtFra: "2022-01-01",
      stengtTil: "2022-12-12",
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee314",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "43242",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "SoloPolo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee315",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "4367",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "SoloPolo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee316",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "7685",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "SoloPolo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee317",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "5435356",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "SoloPolo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.GJENNOMFORES,
      opphav: Opphav.ARENA,
      kontaktpersoner: [],
    },
    {
      id: "a7d63fb0-4366-412c-84b7-7c15518ee318",
      navn: "Arbeidsrettet rehabilitering - Trondheim",
      tiltaksnummer: "987643",
      arrangorOrganisasjonsnummer: "1000",
      arrangorNavn: "Solo",
      tiltakstype: {
        id: "afb69ca8-ddff-45be-9fd0-8f968519468d",
        navn: "TILTAKSTYPENAVN",
        arenaKode: "ABIST",
      },
      startDato: "2022-01-01",
      sluttDato: "2022-12-12",
      tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
      arenaAnsvarligEnhet: "1190",
      navEnheter: [],
      status: TiltaksgjennomforingStatus.APENT_FOR_INNSOK,
      opphav: Opphav.ARENA,
      kontaktpersoner: [],
    },
  ],
};
