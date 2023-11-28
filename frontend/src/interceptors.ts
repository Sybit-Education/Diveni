import { useDiveniStore } from "./store";
import axios, { AxiosRequestHeaders } from "axios";

const setup = (): void => {
  axios.interceptors.request.use((config) => {
    const store = useDiveniStore();
    let tokenId: string | undefined = store.tokenId;
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
