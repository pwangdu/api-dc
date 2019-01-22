import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRack } from 'app/shared/model/rack.model';
import { getEntities as getRacks } from 'app/entities/rack/rack.reducer';
import { getEntity, updateEntity, createEntity, reset } from './datapoint.reducer';
import { IDatapoint } from 'app/shared/model/datapoint.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IDatapointUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IDatapointUpdateState {
  isNew: boolean;
  rackId: string;
}

export class DatapointUpdate extends React.Component<IDatapointUpdateProps, IDatapointUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
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

    this.props.getRacks();
  }

  saveEntity = (event, errors, values) => {
    values.captureTime = new Date(values.captureTime);

    if (errors.length === 0) {
      const { datapointEntity } = this.props;
      const entity = {
        ...datapointEntity,
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
    this.props.history.push('/entity/datapoint');
  };

  render() {
    const { datapointEntity, racks, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="platformApp.datapoint.home.createOrEditLabel">
              <Translate contentKey="platformApp.datapoint.home.createOrEditLabel">Create or edit a Datapoint</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : datapointEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="datapoint-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="tagLabel" for="tag">
                    <Translate contentKey="platformApp.datapoint.tag">Tag</Translate>
                  </Label>
                  <AvField id="datapoint-tag" type="text" name="tag" />
                </AvGroup>
                <AvGroup>
                  <Label id="captureTimeLabel" for="captureTime">
                    <Translate contentKey="platformApp.datapoint.captureTime">Capture Time</Translate>
                  </Label>
                  <AvInput
                    id="datapoint-captureTime"
                    type="datetime-local"
                    className="form-control"
                    name="captureTime"
                    value={isNew ? null : convertDateTimeFromServer(this.props.datapointEntity.captureTime)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="valueLabel" for="value">
                    <Translate contentKey="platformApp.datapoint.value">Value</Translate>
                  </Label>
                  <AvField id="datapoint-value" type="string" className="form-control" name="value" />
                </AvGroup>
                <AvGroup>
                  <Label for="rack.id">
                    <Translate contentKey="platformApp.datapoint.rack">Rack</Translate>
                  </Label>
                  <AvInput id="datapoint-rack" type="select" className="form-control" name="rack.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/datapoint" replace color="info">
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
  racks: storeState.rack.entities,
  datapointEntity: storeState.datapoint.entity,
  loading: storeState.datapoint.loading,
  updating: storeState.datapoint.updating,
  updateSuccess: storeState.datapoint.updateSuccess
});

const mapDispatchToProps = {
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
)(DatapointUpdate);
