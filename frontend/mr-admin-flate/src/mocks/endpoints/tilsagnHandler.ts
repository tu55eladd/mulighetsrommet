import { HttpResponse, PathParams, http } from "msw";
import { TilsagnDto, TilsagnRequest } from "mulighetsrommet-api-client";
import { mockTilsagn } from "../fixtures/mock_tilsagn";

export const tilsagnHandlers = [
  http.put<PathParams, TilsagnRequest>(
    "*/api/v1/intern/tiltaksgjennomforinger/:tiltaksgjennomforingId/tilsagn",
    async ({ request }) => {
      const body = await request.json();
      return HttpResponse.json(body);
    },
  ),
  http.get<PathParams, any, TilsagnDto[]>(
    "*/api/v1/intern/tiltaksgjennomforinger/:tiltaksgjennomforingId/tilsagn",
    async () => {
      return HttpResponse.json(mockTilsagn);
    },
  ),
];
