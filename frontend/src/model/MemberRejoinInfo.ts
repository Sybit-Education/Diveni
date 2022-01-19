import Member from "./Member";
import SessionConfig from "./SessionConfig";
interface MemberRejoinInfo {
  sessionConfig: SessionConfig;
  member: Member;
}

export default MemberRejoinInfo;
