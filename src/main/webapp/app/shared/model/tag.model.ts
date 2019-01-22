export interface ITag {
  id?: number;
  tagId?: string;
  remainingBattery?: number;
}

export const defaultValue: Readonly<ITag> = {};
