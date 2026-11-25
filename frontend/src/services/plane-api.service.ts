import constants from "@/constants";
import { JiraResponseCodeDto } from "@/types";
import axios from "axios";

class PlaneApiService {
  public async connectToPlane(): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/plane/accessToken`
    );
    return response.data;
  }
}

export default new PlaneApiService();
