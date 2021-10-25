import UUID from './UUID';

export default class Member {
    memberID: UUID;

    name: string;

    hexColor: string;

    avatarAnimalAssetName: string;

    currentEstimation: number | null;

    constructor(memberID: UUID, name:string, hexColor: string,
      avatarAnimalAssetName: string,
      currentEstimation: number|null) {
      this.memberID = memberID;
      this.name = name;
      this.hexColor = hexColor;
      this.avatarAnimalAssetName = avatarAnimalAssetName;
      this.currentEstimation = currentEstimation;
    }
}
