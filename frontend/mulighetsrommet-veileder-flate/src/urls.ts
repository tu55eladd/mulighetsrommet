import { getEnvironment } from "./core/api/getEnvironment";

export const DELTAKERREGISTRERING_KOMET = {
  local: "http://localhost:4173",
  development: "https://amt-deltaker-flate.intern.dev.nav.no", // URL til bundle som blir hostet et sted i dev
  production: "", // URL til bundle som blir hostet et sted i prod
};

export const deltakerregistreringKometManifestUrl = `${
  DELTAKERREGISTRERING_KOMET[getEnvironment()]
}/asset-manifest.json`;
