import {
  Tilgjengelighetsstatus,
  TiltaksgjennomforingOppstartstype,
  VeilederflateTiltaksgjennomforing,
} from 'mulighetsrommet-api-client';
import { mockTiltakstyper } from './mockTiltakstyper';

export const mockTiltaksgjennomforinger: VeilederflateTiltaksgjennomforing[] = [
  {
    sanityId: 'f4cea25b-c372-4d4c-8106-535ab10cd586',
    navn: 'Avklaring - Fredrikstad',
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    lokasjon: '1424 SKI',
    tiltakstype: mockTiltakstyper.avklaring,
    sluttdato: '2025-07-09',
    kontaktinfoTiltaksansvarlige: [
      {
        navn: 'Sindre',
        telefonnummer: '12345678',
        _id: '8ea86d71-4c1b-4c8f-81b5-49ec67ef1d17',
        enhet: '123456',
        epost: 'test@example.com',
      },
    ],
  },
  {
    sanityId: '91205ff2-ec72-4a7f-80b8-1c99d8535a06',
    navn: 'Sindres mentorordning med Yoda',
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    lokasjon: 'Oslo',
    tiltakstype: mockTiltakstyper.mentor,
    kontaktinfoTiltaksansvarlige: [
      {
        navn: 'Sindre',
        telefonnummer: '12345678',
        _id: '8ea86d71-4c1b-4c8f-81b5-49ec67ef1d17',
        enhet: '123456',
        epost: 'test@example.com',
      },
    ],
  },
  {
    sanityId: '00097090-1ba8-47a4-a82f-6aaad488994e',
    navn: 'Kurs i overlevelsesteknikk med Lars Monsen',
    lokasjon: '2050 JESSHEIM',
    tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
    tiltakstype: mockTiltakstyper.gruppe_amo,
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    oppstartsdato: '2023-11-01',
    sluttdato: '2023-11-30',
    arrangor: {
      selskapsnavn: 'JOBLEARN AS AVD 813201 ØST-VIKEN KURS',
      lokasjon: '2050 JESSHEIM',
      kontaktperson: {
        navn: 'Ole Testesen',
        telefon: '12345678',
        epost: 'test@example.com',
      },
    },
    estimertVentetid: '',
    kontaktinfoTiltaksansvarlige: [
      {
        navn: 'Pelle Pilotbruker',
        telefonnummer: '48123456',
        enhet: '1928',
        epost: 'pelle.pilotbruker@nav.no',
        _id: '56767',
      },
    ],
  },
  {
    sanityId: '3b597090-1ba8-47a4-a82f-6aaad488994e',
    navn: 'VTA hos Fretex',
    lokasjon: '2050',
    tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
    tiltakstype: mockTiltakstyper.VTA,
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    oppstartsdato: '2023-11-01',
    sluttdato: '2023-11-30',
    arrangor: {
      selskapsnavn: 'FRETEX',
      lokasjon: '2050 JESSHEIM',
      kontaktperson: {
        navn: 'Ole Testesen',
        telefon: '12345678',
        epost: 'test@example.com',
      },
    },
    estimertVentetid: '',
    kontaktinfoTiltaksansvarlige: [
      {
        navn: 'Pelle Pilotbruker',
        telefonnummer: '48123456',
        enhet: '1928',
        epost: 'pelle.pilotbruker@nav.no',
        _id: '56767',
      },
    ],
    faneinnhold: {
      forHvem: [
        {
          style: 'normal',
          children: [
            {
              text: 'Tilbudet kan være aktuelt for deg som har uføretrygd, eller i nær framtid forventer å få uføretrygd, og trenger spesiell tilrettelegging og oppfølging.',
              _type: 'span',
            },
          ],
          _type: 'block',
        },
        {
          children: [
            {
              _type: 'span',
              text: 'Når du deltar på tiltaket varig tilrettelagt arbeid, får du en arbeidskontrakt på lik linje med andre arbeidstakere etter arbeidsmiljøloven.',
            },
          ],
          _type: 'block',
          style: 'normal',
        },
      ],
    detaljerOgInnhold: [
        {
          style: 'normal',
          children: [ { text: 'Virksomheten skal gi tilpassede oppgaver etter den ansattes ønsker, behov og forutsetninger.', _type: 'span', }, ],
          _type: 'block',
        },
      ],
    pameldingOgVarighet: [
        {
          style: 'normal',
          children: [ { text: 'Ta kontakt med NAV-kontoret der du bor. NAV vurderer sammen med deg om du har behov for tiltaket. NAV avgjør om du får tilbudet.', _type: 'span', }, ],
          _type: 'block',
        },
      ],
    }
  },
  {
    sanityId: 'ff887090-1ba8-47a4-a82f-6aaad488994e',
    navn: 'Jobbklubb (med Lars Monsen)',
    lokasjon: 'Kautokeino',
    tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
    tiltakstype: mockTiltakstyper.jobbklubb,
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    oppstartsdato: '2022-11-01',
    sluttdato: '2030-11-30',
    arrangor: {
      selskapsnavn: 'LARS MONSEN AS AVD FINNMARK',
      lokasjon: 'KAUTOKEINO',
      kontaktperson: {
        navn: 'Mona Monsen',
        telefon: '12345678',
        epost: 'test@example.com',
      },
    },
    estimertVentetid: '',
    kontaktinfoTiltaksansvarlige: [
      {
        navn: 'Pelle Pilotbruker',
        telefonnummer: '48123456',
        enhet: '1928',
        epost: 'pelle.pilotbruker@nav.no',
        _id: '56767',
      },
    ],
  },
  {
    sanityId: 'bdfa7090-1ba8-47a4-a82f-6aaad488994e',
    navn: 'AFT',
    lokasjon: 'Sinsen',
    tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
    tiltakstype: mockTiltakstyper.AFT,
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    oppstartsdato: '2022-11-01',
    sluttdato: '2030-11-30',
    arrangor: {
      selskapsnavn: 'AFT GRUPPEN NORWAY',
      lokasjon: 'Sinsen',
      kontaktperson: {
        navn: 'Louise Gran',
        telefon: '12345678',
        epost: 'test@example.com',
      },
    },
    estimertVentetid: '',
    kontaktinfoTiltaksansvarlige: [
      {
        navn: 'Pelle Pilotbruker',
        telefonnummer: '48123456',
        enhet: '1928',
        epost: 'pelle.pilotbruker@nav.no',
        _id: '56767',
      },
    ],
  },
  {
    sanityId: 'f1887090-1ba8-47a4-a82f-6aaad488994e',
    navn: 'Oppæring Fag og Yrke',
    lokasjon: 'Oslo',
    tilgjengelighet: Tilgjengelighetsstatus.LEDIG,
    tiltakstype: mockTiltakstyper.OpplaringEnkeltplassFagOgYrke,
    oppstart: TiltaksgjennomforingOppstartstype.LOPENDE,
    oppstartsdato: '2022-11-01',
    sluttdato: '2030-11-30',
    estimertVentetid: '',
  },
];
