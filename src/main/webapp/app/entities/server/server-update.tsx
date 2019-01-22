import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ITag } from 'app/shared/model/tag.model';
import { getEntities as getTags } from 'app/entities/tag/tag.reducer';
import { getEntity, updateEntity, createEntity, reset } from './server.reducer';
import { IServer } from 'app/shared/model/server.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IServerUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IServerUpdateState {
  isNew: boolean;
  tagId: string;
}

export class ServerUpdate extends React.Component<IServerUpdateProps, IServerUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      tagId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getTags();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { serverEntity } = this.props;
      const entity = {
        ...serverEntity,
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
    this.props.history.push('/entity/server');
  };

  render() {
    const { serverEntity, tags, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="platformApp.server.home.createOrEditLabel">
              <Translate contentKey="platformApp.server.home.createOrEditLabel">Create or edit a Server</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : serverEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="server-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="serverIdLabel" for="serverId">
                    <Translate contentKey="platformApp.server.serverId">Server Id</Translate>
                  </Label>
                  <AvField id="server-serverId" type="text" name="serverId" />
                </AvGroup>
                <AvGroup>
                  <Label id="serverModelLabel" for="serverModel">
                    <Translate contentKey="platformApp.server.serverModel">Server Model</Translate>
                  </Label>
                  <AvField id="server-serverModel" type="text" name="serverModel" />
                </AvGroup>
                <AvGroup>
                  <Label id="serverManufacturerLabel" for="serverManufacturer">
                    <Translate contentKey="platformApp.server.serverManufacturer">Server Manufacturer</Translate>
                  </Label>
                  <AvField id="server-serverManufacturer" type="text" name="serverManufacturer" />
                </AvGroup>
                <AvGroup>
                  <Label for="tag.id">
                    <Translate contentKey="platformApp.server.tag">Tag</Translate>
                  </Label>
                  <AvInput id="server-tag" type="select" className="form-control" name="tag.id">
                    <option value="" key="0" />
                    {tags
                      ? tags.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/server" replace color="info">
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
  tags: storeState.tag.entities,
  serverEntity: storeState.server.entity,
  loading: storeState.server.loading,
  updating: storeState.server.updating,
  updateSuccess: storeState.server.updateSuccess
});

const mapDispatchToProps = {
  getTags,
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
)(ServerUpdate);
