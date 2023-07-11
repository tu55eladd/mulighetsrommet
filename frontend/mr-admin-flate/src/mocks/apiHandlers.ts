import { ansattHandlers } from "./endpoints/ansattHandler";
import { avtaleHandlers } from "./endpoints/avtaleHandlers";
import { enhetHandlers } from "./endpoints/enhetHandlers";
import { notificationHandlers } from "./endpoints/notificationHandlers";
import { tiltaksgjennomforingHandlers } from "./endpoints/tiltaksgjennomforingHandlers";
import { tiltakstypeHandlers } from "./endpoints/tiltakstyperHandlers";
import { utkastHandlers } from "./endpoints/utkastHandlers";
import { virksomhetHandlers } from "./endpoints/virksomhetHandlers";

export const apiHandlers = [
  ...tiltakstypeHandlers,
  ...avtaleHandlers,
  ...tiltaksgjennomforingHandlers,
  ...enhetHandlers,
  ...ansattHandlers,
  ...notificationHandlers,
  ...virksomhetHandlers,
  ...utkastHandlers,
];
