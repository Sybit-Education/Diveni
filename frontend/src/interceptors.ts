import axios from "axios";
import store from "./store";

const setup = (): void => {
  axios.interceptors.request.use((config) => {
    const tokenId = store.state.tokenId;
    if (!config.headers) {
      config.headers = {};
    }
    if (tokenId) {
      config.headers["X-Token-ID"] = tokenId;
    }
    return config;
  });
};

export default setup;
