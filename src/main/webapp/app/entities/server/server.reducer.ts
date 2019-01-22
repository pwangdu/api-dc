import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IServer, defaultValue } from 'app/shared/model/server.model';

export const ACTION_TYPES = {
  SEARCH_SERVERS: 'server/SEARCH_SERVERS',
  FETCH_SERVER_LIST: 'server/FETCH_SERVER_LIST',
  FETCH_SERVER: 'server/FETCH_SERVER',
  CREATE_SERVER: 'server/CREATE_SERVER',
  UPDATE_SERVER: 'server/UPDATE_SERVER',
  DELETE_SERVER: 'server/DELETE_SERVER',
  RESET: 'server/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IServer>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type ServerState = Readonly<typeof initialState>;

// Reducer

export default (state: ServerState = initialState, action): ServerState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_SERVERS):
    case REQUEST(ACTION_TYPES.FETCH_SERVER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_SERVER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_SERVER):
    case REQUEST(ACTION_TYPES.UPDATE_SERVER):
    case REQUEST(ACTION_TYPES.DELETE_SERVER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_SERVERS):
    case FAILURE(ACTION_TYPES.FETCH_SERVER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_SERVER):
    case FAILURE(ACTION_TYPES.CREATE_SERVER):
    case FAILURE(ACTION_TYPES.UPDATE_SERVER):
    case FAILURE(ACTION_TYPES.DELETE_SERVER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_SERVERS):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_SERVER_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_SERVER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_SERVER):
    case SUCCESS(ACTION_TYPES.UPDATE_SERVER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_SERVER):
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

const apiUrl = 'api/servers';
const apiSearchUrl = 'api/_search/servers';

// Actions

export const getSearchEntities: ICrudSearchAction<IServer> = query => ({
  type: ACTION_TYPES.SEARCH_SERVERS,
  payload: axios.get<IServer>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IServer> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_SERVER_LIST,
    payload: axios.get<IServer>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IServer> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_SERVER,
    payload: axios.get<IServer>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IServer> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_SERVER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IServer> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_SERVER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IServer> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_SERVER,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
