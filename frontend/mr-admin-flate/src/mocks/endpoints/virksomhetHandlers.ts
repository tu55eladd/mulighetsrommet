import { http, HttpResponse, PathParams } from "msw";
import { BrregVirksomhet, VirksomhetKontaktperson } from "mulighetsrommet-api-client";
import { mockVirksomhetKontaktperson } from "../fixtures/mock_virksomhet_kontaktperson";
import { mockVirksomheter } from "../fixtures/mock_virksomheter";

export const virksomhetHandlers = [
  http.get<{ sok: string }, BrregVirksomhet[]>(
    "*/api/v1/internal/virksomhet/sok/:sok",
    ({ params }) => {
      return HttpResponse.json(
        Object.values(mockVirksomheter).filter((enhet) =>
          enhet.navn?.toLowerCase().includes(params.sok.toLocaleLowerCase()),
        ),
      );
    },
  ),
  http.get<PathParams, BrregVirksomhet | undefined>(
    "*/api/v1/internal/virksomhet/:orgnr",
    ({ params }) => {
      return HttpResponse.json(
        Object.values(mockVirksomheter).find((enhet) => enhet.organisasjonsnummer === params.orgnr),
      );
    },
  ),
  http.get<PathParams, BrregVirksomhet[] | undefined>("*/api/v1/internal/virksomhet", () =>
    HttpResponse.json(Object.values(mockVirksomheter)),
  ),

  http.get<PathParams, VirksomhetKontaktperson[]>("*/api/v1/internal/*/kontaktperson", () =>
    HttpResponse.json(mockVirksomhetKontaktperson),
  ),
];
