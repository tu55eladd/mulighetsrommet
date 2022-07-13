import { createRoot } from 'react-dom/client';
import React from 'react';
import App from './App';
import '@navikt/ds-css';
import { OpenAPI } from 'mulighetsrommet-api-client';
import Navspa from '@navikt/navspa';
import { worker } from './mock/worker';
import { APPLICATION_NAME } from './constants';
import { headers, toRecord } from './api/headers';

OpenAPI.HEADERS = toRecord(headers);

OpenAPI.BASE = String(import.meta.env.VITE_MULIGHETSROMMET_API_BASE ?? '');

if (import.meta.env.VITE_MULIGHETSROMMET_API_MOCK === 'true') {
  worker.start();
}

Navspa.eksporter(APPLICATION_NAME, App);

// Only mount the application when the root node is available
const container = document.getElementById(APPLICATION_NAME);
if (container) {
  const root = createRoot(container);
  const MulighetsrommetVeilederFlate = Navspa.importer(APPLICATION_NAME);

  root.render(<MulighetsrommetVeilederFlate />);
}
