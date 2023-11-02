import axios from "axios";
import { useDiveniStore } from "./store";

const setup = (): void => {
  axios.interceptors.request.use((config) => {
    const store = useDiveniStore();
    let tokenId: string | undefined = store.tokenId;
    if (!tokenId) {
      tokenId = localStorage.getItem("tokenId") || undefined;
    }
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
