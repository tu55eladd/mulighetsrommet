import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import '@navikt/ds-css';
import './index.less';
import { OpenAPI } from 'mulighetsrommet-api-client';
// import Navspa from '@navikt/navspa';
import { worker } from './mock/worker';

OpenAPI.BASE = String(import.meta.env.VITE_BACKEND_API_ROOT ?? '');

if (import.meta.env.VITE_ENABLE_MOCK === 'true') {
  worker.start();
}

ReactDOM.render(<App />, document.getElementById('mulighetsrommet-root'));

// TODO skal se på dette sammen med Håkon
// else if (import.meta.env.VITE_ENABLE_MOCK === 'true') {
//   Navspa.eksporter('mulighetsrommet-flate', App);
// }
