import axios, {AxiosRequestHeaders} from "axios";
import store from "./store";

const setup = (): void => {
  axios.interceptors.request.use((config) => {
    let tokenId = store.state.tokenId;
    if (!tokenId) {
      tokenId = localStorage.getItem("tokenId") || undefined;
    }
    if (!config.headers) {
      config.headers = {} as AxiosRequestHeaders;
    }
    if (tokenId) {
      config.headers["X-Token-ID"] = tokenId;
    }
    return config;
  });
};

export default setup;
