import { Moment } from 'moment';
import { IServer } from 'app/shared/model//server.model';
import { IRack } from 'app/shared/model//rack.model';

export interface IMembership {
  id?: number;
  startTime?: Moment;
  endTime?: Moment;
  server?: IServer;
  rack?: IRack;
  duration?: string;
}

export const defaultValue: Readonly<IMembership> = {};
