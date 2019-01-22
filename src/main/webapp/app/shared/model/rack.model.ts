import { IZoneMonitor } from 'app/shared/model//zone-monitor.model';
import { IServer } from 'app/shared/model//server.model';

export interface IRack {
  id?: number;
  rackId?: string;
  zoneMonitor?: IZoneMonitor;
  servers?: IServer[];
  temperature?: number;
  humidity?: number;
}

export const defaultValue: Readonly<IRack> = {};
