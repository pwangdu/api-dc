import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
import sessions, { SessionsState } from 'app/modules/account/sessions/sessions.reducer';
// prettier-ignore
import server, {
  ServerState
} from 'app/entities/server/server.reducer';
// prettier-ignore
import rack, {
  RackState
} from 'app/entities/rack/rack.reducer';
// prettier-ignore
import zoneMonitor, {
  ZoneMonitorState
} from 'app/entities/zone-monitor/zone-monitor.reducer';
// prettier-ignore
import tag, {
  TagState
} from 'app/entities/tag/tag.reducer';
// prettier-ignore
import membership, {
  MembershipState
} from 'app/entities/membership/membership.reducer';
// prettier-ignore
import datapoint, {
  DatapointState
} from 'app/entities/datapoint/datapoint.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly locale: LocaleState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly register: RegisterState;
  readonly activate: ActivateState;
  readonly passwordReset: PasswordResetState;
  readonly password: PasswordState;
  readonly settings: SettingsState;
  readonly sessions: SessionsState;
  readonly server: ServerState;
  readonly rack: RackState;
  readonly zoneMonitor: ZoneMonitorState;
  readonly tag: TagState;
  readonly membership: MembershipState;
  readonly datapoint: DatapointState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  sessions,
  server,
  rack,
  zoneMonitor,
  tag,
  membership,
  datapoint,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar
});

export default rootReducer;
