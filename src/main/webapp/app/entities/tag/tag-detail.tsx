import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './tag.reducer';
import { ITag } from 'app/shared/model/tag.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITagDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class TagDetail extends React.Component<ITagDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { tagEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="platformApp.tag.detail.title">Tag</Translate> [<b>{tagEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="tagId">
                <Translate contentKey="platformApp.tag.tagId">Tag Id</Translate>
              </span>
            </dt>
            <dd>{tagEntity.tagId}</dd>
            <dt>
              <span id="remainingBattery">
                <Translate contentKey="platformApp.tag.remainingBattery">Remaining Battery</Translate>
              </span>
            </dt>
            <dd>{tagEntity.remainingBattery}</dd>
          </dl>
          <Button tag={Link} to="/entity/tag" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/tag/${tagEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ tag }: IRootState) => ({
  tagEntity: tag.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TagDetail);
