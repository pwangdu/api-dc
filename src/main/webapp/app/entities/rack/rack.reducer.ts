import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IRack, defaultValue } from 'app/shared/model/rack.model';

export const ACTION_TYPES = {
  SEARCH_RACKS: 'rack/SEARCH_RACKS',
  FETCH_RACK_LIST: 'rack/FETCH_RACK_LIST',
  FETCH_RACK: 'rack/FETCH_RACK',
  CREATE_RACK: 'rack/CREATE_RACK',
  UPDATE_RACK: 'rack/UPDATE_RACK',
  DELETE_RACK: 'rack/DELETE_RACK',
  RESET: 'rack/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRack>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type RackState = Readonly<typeof initialState>;

// Reducer

export default (state: RackState = initialState, action): RackState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_RACKS):
    case REQUEST(ACTION_TYPES.FETCH_RACK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RACK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_RACK):
    case REQUEST(ACTION_TYPES.UPDATE_RACK):
    case REQUEST(ACTION_TYPES.DELETE_RACK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_RACKS):
    case FAILURE(ACTION_TYPES.FETCH_RACK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RACK):
    case FAILURE(ACTION_TYPES.CREATE_RACK):
    case FAILURE(ACTION_TYPES.UPDATE_RACK):
    case FAILURE(ACTION_TYPES.DELETE_RACK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_RACKS):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_RACK_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_RACK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_RACK):
    case SUCCESS(ACTION_TYPES.UPDATE_RACK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_RACK):
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

const apiUrl = 'api/racks';
const apiSearchUrl = 'api/_search/racks';

// Actions

export const getSearchEntities: ICrudSearchAction<IRack> = query => ({
  type: ACTION_TYPES.SEARCH_RACKS,
  payload: axios.get<IRack>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IRack> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RACK_LIST,
    payload: axios.get<IRack>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IRack> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RACK,
    payload: axios.get<IRack>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IRack> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RACK,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRack> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RACK,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRack> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RACK,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
