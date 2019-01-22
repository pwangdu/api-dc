import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Server from './server';
import Rack from './rack';
import ZoneMonitor from './zone-monitor';
import Tag from './tag';
import Membership from './membership';
import Datapoint from './datapoint';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/server`} component={Server} />
      <ErrorBoundaryRoute path={`${match.url}/rack`} component={Rack} />
      <ErrorBoundaryRoute path={`${match.url}/zone-monitor`} component={ZoneMonitor} />
      <ErrorBoundaryRoute path={`${match.url}/tag`} component={Tag} />
      <ErrorBoundaryRoute path={`${match.url}/membership`} component={Membership} />
      <ErrorBoundaryRoute path={`${match.url}/datapoint`} component={Datapoint} />
      {/* jhipster-needle-add-route-path - JHipster will routes here */}
    </Switch>
  </div>
);

export default Routes;
