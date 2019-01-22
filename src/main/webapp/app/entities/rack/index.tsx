import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Rack from './rack';
import RackDetail from './rack-detail';
import RackUpdate from './rack-update';
import RackDeleteDialog from './rack-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RackUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RackUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RackDetail} />
      <ErrorBoundaryRoute path={match.url} component={Rack} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={RackDeleteDialog} />
  </>
);

export default Routes;
