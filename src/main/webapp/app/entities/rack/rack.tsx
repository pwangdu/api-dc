import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table, Progress } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import {
  Translate,
  translate,
  ICrudSearchAction,
  ICrudGetAllAction,
  getSortState,
  IPaginationBaseState,
  getPaginationItemsNumber,
  JhiPagination,
  TextFormat
} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './rack.reducer';
import { IRack } from 'app/shared/model/rack.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT, APP_WHOLE_NUMBER_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import server from '../server/server';

export interface IRackProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> { }

export interface IRackState extends IPaginationBaseState {
  search: string;
}

export class Rack extends React.Component<IRackProps, IRackState> {
  state: IRackState = {
    search: '',
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
    this.getEntities();
  }

  search = () => {
    if (this.state.search) {
      this.props.getSearchEntities(this.state.search);
    }
  };

  clear = () => {
    this.props.getEntities();
    this.setState({
      search: ''
    });
  };

  handleSearch = event => this.setState({ search: event.target.value });

  sort = prop => () => {
    this.setState(
      {
        order: this.state.order === 'asc' ? 'desc' : 'asc',
        sort: prop
      },
      () => this.sortEntities()
    );
  };

  sortEntities() {
    this.getEntities();
    this.props.history.push(`${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`);
  }

  handlePagination = activePage => this.setState({ activePage }, () => this.sortEntities());

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order } = this.state;
    this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
  };

  render() {
    const { rackList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="rack-heading">
          <Translate contentKey="platformApp.rack.home.title">Racks</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="platformApp.rack.home.createLabel">Create new Rack</Translate>
          </Link>
        </h2>
        <Row>
          <Col sm="12">
            <AvForm onSubmit={this.search}>
              <AvGroup>
                <InputGroup>
                  <AvInput
                    type="text"
                    name="search"
                    value={this.state.search}
                    onChange={this.handleSearch}
                    placeholder={translate('platformApp.rack.home.search')}
                  />
                  <Button className="input-group-addon">
                    <FontAwesomeIcon icon="search" />
                  </Button>
                  <Button type="reset" className="input-group-addon" onClick={this.clear}>
                    <FontAwesomeIcon icon="trash" />
                  </Button>
                </InputGroup>
              </AvGroup>
            </AvForm>
          </Col>
        </Row>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={this.sort('id')}>
                  <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('rackId')}>
                  <Translate contentKey="platformApp.rack.rackId">Rack Id</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="platformApp.rack.zoneMonitor">Zone Monitor</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Servers
                </th>
                <th>
                  Occupancy
                </th>
                <th>
                  Temperature
                </th>
                <th>
                  Humidity
                </th>
              </tr>
            </thead>
            <tbody>
              {rackList.map((rack, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${rack.id}`} color="link" size="sm">
                      {rack.id}
                    </Button>
                  </td>
                  <td>{rack.rackId}</td>
                  <td>{rack.zoneMonitor ? <Link to={`zone-monitor/${rack.zoneMonitor.id}`}>{rack.zoneMonitor.zoneMonitorId}</Link> : ''}</td>
                  <td>
                    <div className="btn-group flex-btn-group-container">
                      {rack.servers
                        ? rack.servers.map(otherEntity => (
                          <Button tag={Link} to={`server/${otherEntity.id}`} color="link" size="sm">
                            {otherEntity.serverId}
                          </Button>
                        ))
                        : null}
                    </div>
                  </td>
                  <td>
                    <Progress
                      animated
                      value={rack.servers.length}
                      min="0"
                      max="10"
                      color="success"
                    >
                      <span>
                        <TextFormat
                          value={rack.servers.length / 20 * 100}
                          type="number"
                          format={APP_WHOLE_NUMBER_FORMAT}
                        />
                        %
                      </span>
                    </Progress>
                  </td>
                  <td />
                  <td />
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
        <Row className="justify-content-center">
          <JhiPagination
            items={getPaginationItemsNumber(totalItems, this.state.itemsPerPage)}
            activePage={this.state.activePage}
            onSelect={this.handlePagination}
            maxButtons={5}
          />
        </Row>
      </div>
    );
  }
}

const mapStateToProps = ({ rack }: IRootState) => ({
  rackList: rack.entities,
  totalItems: rack.totalItems
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Rack);
