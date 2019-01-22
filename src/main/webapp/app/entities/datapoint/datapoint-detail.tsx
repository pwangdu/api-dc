import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './datapoint.reducer';
import { IDatapoint } from 'app/shared/model/datapoint.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IDatapointDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class DatapointDetail extends React.Component<IDatapointDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { datapointEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="platformApp.datapoint.detail.title">Datapoint</Translate> [<b>{datapointEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="tag">
                <Translate contentKey="platformApp.datapoint.tag">Tag</Translate>
              </span>
            </dt>
            <dd>{datapointEntity.tag}</dd>
            <dt>
              <span id="captureTime">
                <Translate contentKey="platformApp.datapoint.captureTime">Capture Time</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={datapointEntity.captureTime} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="value">
                <Translate contentKey="platformApp.datapoint.value">Value</Translate>
              </span>
            </dt>
            <dd>{datapointEntity.value}</dd>
            <dt>
              <Translate contentKey="platformApp.datapoint.rack">Rack</Translate>
            </dt>
            <dd>{datapointEntity.rack ? datapointEntity.rack.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/datapoint" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/datapoint/${datapointEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ datapoint }: IRootState) => ({
  datapointEntity: datapoint.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DatapointDetail);
