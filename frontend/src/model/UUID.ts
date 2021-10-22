import * as uuid from 'uuid';

export default class UUID {
  value: string

  constructor(s:string) {
    this.value = s;
  }

  static fromString(s:string): UUID | null {
    try {
      uuid.parse(s);
      return new UUID(s);
    } catch (error) {
      return null;
    }
  }
}
