import Member from "./Member";
import SessionConfig from "./SessionConfig";
interface MemberRejoinInfo {
  sessionID: string;
  sessionConfig: SessionConfig;
  member: Member;
}

export default MemberRejoinInfo;
