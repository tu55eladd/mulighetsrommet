import { rest, DefaultBodyType, PathParams } from 'msw';
import { DelMedBruker, DialogResponse } from 'mulighetsrommet-api-client';

export const delMedBrukerHandlers = [
  rest.post<DelMedBruker, PathParams, DelMedBruker>('*/api/v1/internal/delMedBruker', async (req, res, ctx) => {
    const data = await req.json<DelMedBruker>();
    return res(ctx.status(200), ctx.json(data));
  }),

  rest.get<DefaultBodyType, PathParams, DelMedBruker>('*/api/v1/internal/delMedBruker/*', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        tiltaksnummer: '29518',
        navident: 'V15555',
        dialogId: '12345',
        bruker_fnr: '11223344557',
        createdAt: new Date(2022, 2, 22).toString(),
      })
    );
  }),

  rest.post<DefaultBodyType, PathParams, DialogResponse>('*/api/v1/internal/dialog', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        id: '12345',
      })
    );
  }),
];
