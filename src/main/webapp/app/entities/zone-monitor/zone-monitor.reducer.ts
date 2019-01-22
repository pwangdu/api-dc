import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IZoneMonitor, defaultValue } from 'app/shared/model/zone-monitor.model';

export const ACTION_TYPES = {
  SEARCH_ZONEMONITORS: 'zoneMonitor/SEARCH_ZONEMONITORS',
  FETCH_ZONEMONITOR_LIST: 'zoneMonitor/FETCH_ZONEMONITOR_LIST',
  FETCH_ZONEMONITOR: 'zoneMonitor/FETCH_ZONEMONITOR',
  CREATE_ZONEMONITOR: 'zoneMonitor/CREATE_ZONEMONITOR',
  UPDATE_ZONEMONITOR: 'zoneMonitor/UPDATE_ZONEMONITOR',
  DELETE_ZONEMONITOR: 'zoneMonitor/DELETE_ZONEMONITOR',
  RESET: 'zoneMonitor/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IZoneMonitor>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ZoneMonitorState = Readonly<typeof initialState>;

// Reducer

export default (state: ZoneMonitorState = initialState, action): ZoneMonitorState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ZONEMONITORS):
    case REQUEST(ACTION_TYPES.FETCH_ZONEMONITOR_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ZONEMONITOR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ZONEMONITOR):
    case REQUEST(ACTION_TYPES.UPDATE_ZONEMONITOR):
    case REQUEST(ACTION_TYPES.DELETE_ZONEMONITOR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_ZONEMONITORS):
    case FAILURE(ACTION_TYPES.FETCH_ZONEMONITOR_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ZONEMONITOR):
    case FAILURE(ACTION_TYPES.CREATE_ZONEMONITOR):
    case FAILURE(ACTION_TYPES.UPDATE_ZONEMONITOR):
    case FAILURE(ACTION_TYPES.DELETE_ZONEMONITOR):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ZONEMONITORS):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ZONEMONITOR_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ZONEMONITOR):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ZONEMONITOR):
    case SUCCESS(ACTION_TYPES.UPDATE_ZONEMONITOR):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ZONEMONITOR):
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

const apiUrl = 'api/zone-monitors';
const apiSearchUrl = 'api/_search/zone-monitors';

// Actions

export const getSearchEntities: ICrudSearchAction<IZoneMonitor> = query => ({
  type: ACTION_TYPES.SEARCH_ZONEMONITORS,
  payload: axios.get<IZoneMonitor>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IZoneMonitor> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ZONEMONITOR_LIST,
  payload: axios.get<IZoneMonitor>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IZoneMonitor> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ZONEMONITOR,
    payload: axios.get<IZoneMonitor>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IZoneMonitor> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ZONEMONITOR,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IZoneMonitor> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ZONEMONITOR,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IZoneMonitor> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ZONEMONITOR,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
