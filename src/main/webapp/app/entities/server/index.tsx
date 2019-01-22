import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Server from './server';
import ServerDetail from './server-detail';
import ServerUpdate from './server-update';
import ServerDeleteDialog from './server-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ServerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ServerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ServerDetail} />
      <ErrorBoundaryRoute path={match.url} component={Server} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ServerDeleteDialog} />
  </>
);

export default Routes;
