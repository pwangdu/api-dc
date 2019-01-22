import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ZoneMonitor from './zone-monitor';
import ZoneMonitorDetail from './zone-monitor-detail';
import ZoneMonitorUpdate from './zone-monitor-update';
import ZoneMonitorDeleteDialog from './zone-monitor-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ZoneMonitorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ZoneMonitorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ZoneMonitorDetail} />
      <ErrorBoundaryRoute path={match.url} component={ZoneMonitor} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ZoneMonitorDeleteDialog} />
  </>
);

export default Routes;
