import { Moment } from 'moment';
import { IRack } from 'app/shared/model//rack.model';

export interface IDatapoint {
  id?: number;
  tag?: string;
  captureTime?: Moment;
  value?: number;
  rack?: IRack;
}

export const defaultValue: Readonly<IDatapoint> = {};
