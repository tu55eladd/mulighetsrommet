import { createClient, SanityClient } from '@sanity/client';
import { rest, RestHandler } from 'msw';
import {
  Ansatt,
  Bruker,
  DelMedBruker,
  DialogResponse,
  HistorikkForBruker,
  Innsatsgruppe,
} from 'mulighetsrommet-api-client';
import { historikk } from '../fixtures/historikk';
import { badReq, ok } from './responses';

export const apiHandlers: RestHandler[] = [
  rest.get<any, any, Bruker>('*/api/v1/internal/bruker', (req, res, ctx) => {
    const fnr = req.url.searchParams.get('fnr');

    if (typeof fnr !== 'string') {
      return badReq("'fnr' must be specified");
    }

    return res(
      ctx.status(200),
      ctx.json({
        fnr,
        //En bruker har enten servicegruppe eller innsatsgruppe. Denne kan endres ved behov
        innsatsgruppe: Innsatsgruppe.SITUASJONSBESTEMT_INNSATS,
        // servicegruppe: 'BATT',
        oppfolgingsenhet: {
          navn: 'NAV Lerkendal',
          enhetId: '5702',
        },
        fornavn: 'IHERDIG',
        manuellStatus: {
          erUnderManuellOppfolging: false,
          krrStatus: {
            kanVarsles: true,
            erReservert: false,
          },
        },
      })
    );
  }),

  rest.get<any, any, Ansatt>('*/api/v1/internal/ansatt/me', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        etternavn: 'VEILEDERSEN',
        fornavn: 'VEILEDER',
        ident: 'V12345',
        navn: 'Veiledersen, Veileder',
        tilganger: [],
        hovedenhet: '2990',
        hovedenhetNavn: 'Østfold',
      })
    );
  }),

  rest.post<any, any, DialogResponse>('*/api/v1/internal/dialog', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        id: '12345',
      })
    );
  }),

  rest.get<any, any, any>('*/api/v1/internal/sanity', async req => {
    const query = req.url.searchParams.get('query');

    if (!(typeof query === 'string')) {
      return badReq("'query' must be specified");
    }

    const client = getSanityClient();
    const result = await client.fetch(query, { enhetsId: 'enhet.lokal.5702', fylkeId: 'enhet.fylke.5700' });
    return ok(result);
  }),

  rest.get<any, any, any>('*/api/v1/internal/sanity/innsatsgrupper', async () => {
    const query = `*[_type == "innsatsgruppe" && !(_id in path("drafts.**"))]`;
    const client = getSanityClient();
    const result = await client.fetch(query);
    return ok(result);
  }),

  rest.get<any, any, any>('*/api/v1/internal/sanity/tiltakstyper', async () => {
    const query = `*[_type == "tiltakstype" && !(_id in path("drafts.**"))]`;
    const client = getSanityClient();
    const result = await client.fetch(query);
    return ok(result);
  }),

  rest.get<any, any, any>('*/api/v1/internal/sanity/lokasjoner', async () => {
    const query = `array::unique(*[_type == "tiltaksgjennomforing" && !(_id in path("drafts.**"))
    && ('enhet.lokal.5702' in enheter[]._ref || (enheter[0] == null && 'enhet.fylke.5700' == fylke._ref))]
    {
      lokasjon
    }.lokasjon)`;
    const client = getSanityClient();
    const result = await client.fetch(query);
    return ok(result);
  }),

  rest.get<any, any, HistorikkForBruker[]>('*/api/v1/internal/bruker/historikk', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(historikk));
  }),

  rest.post<DelMedBruker, any, DelMedBruker>('*/api/v1/internal/delMedBruker', async (req, res, ctx) => {
    const data = (await req.json()) as DelMedBruker;
    return res(ctx.status(200), ctx.json(data));
  }),

  rest.get<any, any, DelMedBruker>('*/api/v1/internal/delMedBruker/*', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        tiltaksnummer: '29518',
        navident: 'V15555',
        dialogId: '12345',
        bruker_fnr: '11223344557',
        created_at: new Date(2022, 2, 22).toString(),
      })
    );
  }),
];

let cachedClient: SanityClient | null = null;

function getSanityClient() {
  if (cachedClient) {
    return cachedClient;
  }

  cachedClient = createClient({
    apiVersion: '2022-06-20',
    projectId: import.meta.env.VITE_SANITY_PROJECT_ID,
    dataset: import.meta.env.VITE_SANITY_DATASET,
  });

  return cachedClient;
}
