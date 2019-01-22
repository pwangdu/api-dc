import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './server.reducer';
import { IServer } from 'app/shared/model/server.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IServerDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ServerDetail extends React.Component<IServerDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { serverEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="platformApp.server.detail.title">Server</Translate> [<b>{serverEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="serverId">
                <Translate contentKey="platformApp.server.serverId">Server Id</Translate>
              </span>
            </dt>
            <dd>{serverEntity.serverId}</dd>
            <dt>
              <span id="serverModel">
                <Translate contentKey="platformApp.server.serverModel">Server Model</Translate>
              </span>
            </dt>
            <dd>{serverEntity.serverModel}</dd>
            <dt>
              <span id="serverManufacturer">
                <Translate contentKey="platformApp.server.serverManufacturer">Server Manufacturer</Translate>
              </span>
            </dt>
            <dd>{serverEntity.serverManufacturer}</dd>
            <dt>
              <Translate contentKey="platformApp.server.tag">Tag</Translate>
            </dt>
            <dd>{serverEntity.tag ? serverEntity.tag.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/server" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/server/${serverEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ server }: IRootState) => ({
  serverEntity: server.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ServerDetail);
