import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rack.reducer';
import { IRack } from 'app/shared/model/rack.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRackDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class RackDetail extends React.Component<IRackDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { rackEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="platformApp.rack.detail.title">Rack</Translate> [<b>{rackEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="rackId">
                <Translate contentKey="platformApp.rack.rackId">Rack Id</Translate>
              </span>
            </dt>
            <dd>{rackEntity.rackId}</dd>
            <dt>
              <Translate contentKey="platformApp.rack.zoneMonitor">Zone Monitor</Translate>
            </dt>
            <dd>{rackEntity.zoneMonitor ? rackEntity.zoneMonitor.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/rack" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/rack/${rackEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ rack }: IRootState) => ({
  rackEntity: rack.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(RackDetail);
