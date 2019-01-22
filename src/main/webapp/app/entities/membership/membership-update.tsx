import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IServer } from 'app/shared/model/server.model';
import { getEntities as getServers } from 'app/entities/server/server.reducer';
import { IRack } from 'app/shared/model/rack.model';
import { getEntities as getRacks } from 'app/entities/rack/rack.reducer';
import { getEntity, updateEntity, createEntity, reset } from './membership.reducer';
import { IMembership } from 'app/shared/model/membership.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMembershipUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMembershipUpdateState {
  isNew: boolean;
  serverId: string;
  rackId: string;
}

export class MembershipUpdate extends React.Component<IMembershipUpdateProps, IMembershipUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      serverId: '0',
      rackId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (!this.state.isNew) {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getServers();
    this.props.getRacks();
  }

  saveEntity = (event, errors, values) => {
    values.startTime = new Date(values.startTime);
    values.endTime = new Date(values.endTime);

    if (errors.length === 0) {
      const { membershipEntity } = this.props;
      const entity = {
        ...membershipEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/membership');
  };

  render() {
    const { membershipEntity, servers, racks, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="platformApp.membership.home.createOrEditLabel">
              <Translate contentKey="platformApp.membership.home.createOrEditLabel">Create or edit a Membership</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : membershipEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="membership-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="startTimeLabel" for="startTime">
                    <Translate contentKey="platformApp.membership.startTime">Start Time</Translate>
                  </Label>
                  <AvInput
                    id="membership-startTime"
                    type="datetime-local"
                    className="form-control"
                    name="startTime"
                    value={isNew ? null : convertDateTimeFromServer(this.props.membershipEntity.startTime)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="endTimeLabel" for="endTime">
                    <Translate contentKey="platformApp.membership.endTime">End Time</Translate>
                  </Label>
                  <AvInput
                    id="membership-endTime"
                    type="datetime-local"
                    className="form-control"
                    name="endTime"
                    value={isNew ? null : convertDateTimeFromServer(this.props.membershipEntity.endTime)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="server.id">
                    <Translate contentKey="platformApp.membership.server">Server</Translate>
                  </Label>
                  <AvInput id="membership-server" type="select" className="form-control" name="server.id">
                    <option value="" key="0" />
                    {servers
                      ? servers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="rack.id">
                    <Translate contentKey="platformApp.membership.rack">Rack</Translate>
                  </Label>
                  <AvInput id="membership-rack" type="select" className="form-control" name="rack.id">
                    <option value="" key="0" />
                    {racks
                      ? racks.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/membership" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  servers: storeState.server.entities,
  racks: storeState.rack.entities,
  membershipEntity: storeState.membership.entity,
  loading: storeState.membership.loading,
  updating: storeState.membership.updating,
  updateSuccess: storeState.membership.updateSuccess
});

const mapDispatchToProps = {
  getServers,
  getRacks,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MembershipUpdate);
