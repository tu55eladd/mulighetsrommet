import { headers } from "../headers";
import { useQuery } from "@tanstack/react-query";

export const ENABLE_ADMIN_FLATE = "mulighetsrommet.enable-admin-flate";
export const OPPRETT_AVTALE_ADMIN_FLATE =
  "mulighetsrommet.admin-flate-opprett-avtale";
export const REDIGER_AVTALE_ADMIN_FLATE =
  "mulighetsrommet.admin-flate-rediger-avtale";
export const OPPRETT_TILTAKSGJENNOMFORING_ADMIN_FLATE =
  "mulighetsrommet.admin-flate-opprett-tiltaksgjennomforing";
export const SLETTE_AVTALE = "mulighetsrommet.admin-flate-slett-avtale";
export const REDIGER_TILTAKSGJENNOMFORING_ADMIN_FLATE =
  "mulighetsrommet.admin-flate-rediger-tiltaksgjennomforing";
export const SLETT_TILTAKSGJENNOMFORING_ADMIN_FLATE =
  "mulighetsrommet.admin-flate-slett-tiltaksgjennomforing";
export const VIS_DELTAKERLISTE_KOMET =
  "mulighetsrommet.admin-flate-vis-deltakerliste-fra-komet";

export const ALL_TOGGLES = [
  ENABLE_ADMIN_FLATE,
  OPPRETT_AVTALE_ADMIN_FLATE,
  REDIGER_AVTALE_ADMIN_FLATE,
  OPPRETT_TILTAKSGJENNOMFORING_ADMIN_FLATE,
  SLETTE_AVTALE,
  REDIGER_TILTAKSGJENNOMFORING_ADMIN_FLATE,
  SLETT_TILTAKSGJENNOMFORING_ADMIN_FLATE,
  VIS_DELTAKERLISTE_KOMET,
] as const;

export type Features = Record<(typeof ALL_TOGGLES)[number], boolean>;

export const initialFeatures: Features = {
  "mulighetsrommet.enable-admin-flate": false,
  "mulighetsrommet.admin-flate-opprett-avtale": false,
  "mulighetsrommet.admin-flate-rediger-avtale": false,
  "mulighetsrommet.admin-flate-opprett-tiltaksgjennomforing": false,
  "mulighetsrommet.admin-flate-slett-avtale": false,
  "mulighetsrommet.admin-flate-slett-tiltaksgjennomforing": false,
  "mulighetsrommet.admin-flate-rediger-tiltaksgjennomforing": false,
  "mulighetsrommet.admin-flate-vis-deltakerliste-fra-komet": false,
};

const toggles = ALL_TOGGLES.map((element) => "feature=" + element).join("&");
export const fetchConfig = {
  headers,
};

export const useFeatureToggles = () => {
  return useQuery<Features>(["features"], () =>
    fetch(`/unleash/api/feature?${toggles}`, fetchConfig).then((Response) => {
      return Response.ok ? Response.json() : initialFeatures;
    }),
  );
};
