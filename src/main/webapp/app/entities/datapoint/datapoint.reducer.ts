import axios from 'axios';
import {
  ICrudSearchAction,
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IDatapoint, defaultValue } from 'app/shared/model/datapoint.model';

export const ACTION_TYPES = {
  SEARCH_DATAPOINTS: 'datapoint/SEARCH_DATAPOINTS',
  FETCH_DATAPOINT_LIST: 'datapoint/FETCH_DATAPOINT_LIST',
  FETCH_DATAPOINT: 'datapoint/FETCH_DATAPOINT',
  CREATE_DATAPOINT: 'datapoint/CREATE_DATAPOINT',
  UPDATE_DATAPOINT: 'datapoint/UPDATE_DATAPOINT',
  DELETE_DATAPOINT: 'datapoint/DELETE_DATAPOINT',
  RESET: 'datapoint/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IDatapoint>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type DatapointState = Readonly<typeof initialState>;

// Reducer

export default (state: DatapointState = initialState, action): DatapointState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_DATAPOINTS):
    case REQUEST(ACTION_TYPES.FETCH_DATAPOINT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_DATAPOINT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_DATAPOINT):
    case REQUEST(ACTION_TYPES.UPDATE_DATAPOINT):
    case REQUEST(ACTION_TYPES.DELETE_DATAPOINT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_DATAPOINTS):
    case FAILURE(ACTION_TYPES.FETCH_DATAPOINT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_DATAPOINT):
    case FAILURE(ACTION_TYPES.CREATE_DATAPOINT):
    case FAILURE(ACTION_TYPES.UPDATE_DATAPOINT):
    case FAILURE(ACTION_TYPES.DELETE_DATAPOINT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_DATAPOINTS):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_DATAPOINT_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_DATAPOINT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_DATAPOINT):
    case SUCCESS(ACTION_TYPES.UPDATE_DATAPOINT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_DATAPOINT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/datapoints';
const apiSearchUrl = 'api/_search/datapoints';

// Actions

export const getSearchEntities: ICrudSearchAction<IDatapoint> = query => ({
  type: ACTION_TYPES.SEARCH_DATAPOINTS,
  payload: axios.get<IDatapoint>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IDatapoint> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_DATAPOINT_LIST,
    payload: axios.get<IDatapoint>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IDatapoint> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_DATAPOINT,
    payload: axios.get<IDatapoint>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IDatapoint> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_DATAPOINT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IDatapoint> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_DATAPOINT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IDatapoint> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_DATAPOINT,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
