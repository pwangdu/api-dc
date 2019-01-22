import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './zone-monitor.reducer';
import { IZoneMonitor } from 'app/shared/model/zone-monitor.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IZoneMonitorDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ZoneMonitorDetail extends React.Component<IZoneMonitorDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { zoneMonitorEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="platformApp.zoneMonitor.detail.title">ZoneMonitor</Translate> [<b>{zoneMonitorEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="zoneMonitorId">
                <Translate contentKey="platformApp.zoneMonitor.zoneMonitorId">Zone Monitor Id</Translate>
              </span>
            </dt>
            <dd>{zoneMonitorEntity.zoneMonitorId}</dd>
          </dl>
          <Button tag={Link} to="/entity/zone-monitor" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/zone-monitor/${zoneMonitorEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ zoneMonitor }: IRootState) => ({
  zoneMonitorEntity: zoneMonitor.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ZoneMonitorDetail);
