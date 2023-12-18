import { Endringshistorikk } from "mulighetsrommet-api-client";
import { mockBetabruker } from "./mock_ansatt";

export const mockEndringshistorikkForTiltaksgjennomforing: Endringshistorikk = {
  entries: [
    {
      editedAt: new Date(2023, 11, 17, 9, 45).toISOString(),
      editedBy: {
        navIdent: mockBetabruker.navIdent,
        navn: `${mockBetabruker.fornavn} ${mockBetabruker.etternavn}`,
      },
      id: crypto.randomUUID(),
      operation: "Endret tiltaksgjennomføring",
    },
    {
      editedAt: new Date(2023, 11, 15, 16, 17).toISOString(),
      editedBy: {
        navIdent: mockBetabruker.navIdent,
        navn: `${mockBetabruker.fornavn} ${mockBetabruker.etternavn}`,
      },
      id: crypto.randomUUID(),
      operation: "Endret tiltaksgjennomføring",
    },
    {
      editedAt: new Date(2023, 9, 16, 12, 15, 0).toISOString(),
      editedBy: {
        navIdent: mockBetabruker.navIdent,
        navn: `${mockBetabruker.fornavn} ${mockBetabruker.etternavn}`,
      },
      id: crypto.randomUUID(),
      operation: "Endret tiltaksgjennomføring",
    },
    {
      editedAt: new Date(2023, 5, 10, 8, 14, 0).toISOString(),
      editedBy: {
        navIdent: mockBetabruker.navIdent,
        navn: `${mockBetabruker.fornavn} ${mockBetabruker.etternavn}`,
      },
      id: crypto.randomUUID(),
      operation: "Opprettet tiltaksgjennomføring",
    },
  ],
};