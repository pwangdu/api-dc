import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Membership from './membership';
import MembershipDetail from './membership-detail';
import MembershipUpdate from './membership-update';
import MembershipDeleteDialog from './membership-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MembershipUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MembershipUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MembershipDetail} />
      <ErrorBoundaryRoute path={match.url} component={Membership} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MembershipDeleteDialog} />
  </>
);

export default Routes;
