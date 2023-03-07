import groq from 'groq';
import { useAtom } from 'jotai';
import { Innsatsgruppe } from 'mulighetsrommet-api-client';
import { tiltaksgjennomforingsfilter, Tiltaksgjennomforingsfiltergruppe } from '../../atoms/atoms';
import { Tiltaksgjennomforing } from '../models';
import { useHentBrukerdata } from './useHentBrukerdata';
import { useSanity } from './useSanity';

export default function useTiltaksgjennomforinger() {
  const [filter] = useAtom(tiltaksgjennomforingsfilter);
  const brukerData = useHentBrukerdata();

  const sanityQueryString = groq`*[_type == "tiltaksgjennomforing" && !(_id in path("drafts.**"))
  ${byggInnsatsgruppeFilter(filter.innsatsgruppe?.nokkel)}
  ${byggTiltakstypeFilter(filter.tiltakstyper)}
  ${byggSokefilter(filter.search)}
  ${byggLokasjonsFilter(filter.lokasjoner ?? [])}
  ${byggEnhetOgFylkeFilter()}
  ]
  {
    _id,
    tiltaksgjennomforingNavn,
    lokasjon,
    oppstart,
    oppstartsdato,
    estimert_ventetid,
    "tiltaksnummer": tiltaksnummer.current,
    kontaktinfoArrangor->{selskapsnavn},
    tiltakstype->{tiltakstypeNavn},
    tilgjengelighetsstatus
  }`;

  return useSanity<Tiltaksgjennomforing[]>(sanityQueryString, {
    enabled: !!brukerData.data?.oppfolgingsenhet,
  });
}

function byggEnhetOgFylkeFilter(): string {
  return groq`&& ($enhetsId in enheter[]._ref || (enheter[0] == null && $fylkeId == fylke._ref))`;
}

function byggLokasjonsFilter(lokasjoner: Tiltaksgjennomforingsfiltergruppe<string>[]): string {
  if (lokasjoner.length === 0) return '';

  const lokasjonsStreng = lokasjoner.map(({ tittel }) => `"${tittel}"`).join(', ');

  return groq`&& lokasjon in [${lokasjonsStreng}]`;
}

function byggInnsatsgruppeFilter(innsatsgruppe?: Innsatsgruppe): string {
  if (!innsatsgruppe) return '';

  const innsatsgrupperISok = utledInnsatsgrupperFraInnsatsgruppe(innsatsgruppe)
    .map(nokkel => `"${nokkel}"`)
    .join(', ');
  return groq`&& tiltakstype->innsatsgruppe->nokkel in [${innsatsgrupperISok}]`;
}

export function utledInnsatsgrupperFraInnsatsgruppe(innsatsgruppe: Innsatsgruppe): Innsatsgruppe[] {
  switch (innsatsgruppe) {
    case 'STANDARD_INNSATS':
      return [Innsatsgruppe.STANDARD_INNSATS];
    case 'SITUASJONSBESTEMT_INNSATS':
      return [Innsatsgruppe.STANDARD_INNSATS, Innsatsgruppe.SITUASJONSBESTEMT_INNSATS];
    case 'SPESIELT_TILPASSET_INNSATS':
      return [
        Innsatsgruppe.SITUASJONSBESTEMT_INNSATS,
        Innsatsgruppe.STANDARD_INNSATS,
        Innsatsgruppe.SITUASJONSBESTEMT_INNSATS,
        Innsatsgruppe.SPESIELT_TILPASSET_INNSATS,
      ];
    case 'VARIG_TILPASSET_INNSATS':
      return [
        Innsatsgruppe.STANDARD_INNSATS,
        Innsatsgruppe.SITUASJONSBESTEMT_INNSATS,
        Innsatsgruppe.SPESIELT_TILPASSET_INNSATS,
        Innsatsgruppe.VARIG_TILPASSET_INNSATS,
      ];
    default:
      return [];
  }
}

function byggTiltakstypeFilter(tiltakstyper: Tiltaksgjennomforingsfiltergruppe<string>[]): string {
  return tiltakstyper.length > 0 ? groq`&& tiltakstype->_id in [${idSomListe(tiltakstyper)}]` : '';
}

function byggSokefilter(search: string | undefined) {
  return search
    ? groq`&& [tiltaksgjennomforingNavn, string(tiltaksnummer.current), tiltakstype->tiltakstypeNavn, lokasjon, kontaktinfoArrangor->selskapsnavn, oppstartsdato] match "*${search}*"`
    : '';
}

function idSomListe(elementer: Tiltaksgjennomforingsfiltergruppe<string>[]): string {
  return elementer.map(({ id }) => `"${id}"`).join(', ');
}
