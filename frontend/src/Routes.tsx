import React from 'react';
import { Route, Switch } from 'react-router-dom';
import TiltaksgjennomforingDetaljer from './views/tiltaksgjennomforing-detaljer/TiltaksgjennomforingDetaljer';
import TiltaksvariantDetaljer from './views/tiltaksvariant-detaljer/TiltaksvariantDetaljer';
import ViewTiltaksvariantOversikt from './views/tiltaksvariant-oversikt/ViewTiltaksvariantOversikt';
import { OpprettTiltaksvariant } from './views/tiltaksvariant-redigering/OpprettTiltaksvariant';
import { RedigerTiltaksvariant } from './views/tiltaksvariant-redigering/RedigerTiltaksvariant';

const Routes = () => {
  return (
    <Switch>
      <Route exact path="/tiltaksvarianter/opprett" component={OpprettTiltaksvariant} />
      <Route exact path="/tiltaksvarianter/:id" component={TiltaksvariantDetaljer} />
      <Route exact path="/tiltaksvarianter/:id/rediger" component={RedigerTiltaksvariant} />
      <Route
        exact
        path="/tiltaksvarianter/:tiltaksvariantId/tiltaksgjennomforinger/:tiltaksgjennomforingsId"
        component={TiltaksgjennomforingDetaljer}
      />
      <Route path="/" component={ViewTiltaksvariantOversikt} />
    </Switch>
  );
};

export default Routes;
