import { ITag } from 'app/shared/model//tag.model';

export interface IServer {
  id?: number;
  serverId?: string;
  serverModel?: string;
  serverManufacturer?: string;
  tag?: ITag;
}

export const defaultValue: Readonly<IServer> = {};
