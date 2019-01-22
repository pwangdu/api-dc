import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Datapoint from './datapoint';
import DatapointDetail from './datapoint-detail';
import DatapointUpdate from './datapoint-update';
import DatapointDeleteDialog from './datapoint-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DatapointUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DatapointUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DatapointDetail} />
      <ErrorBoundaryRoute path={match.url} component={Datapoint} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={DatapointDeleteDialog} />
  </>
);

export default Routes;
