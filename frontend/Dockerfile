FROM node:lts-alpine

RUN npm install -g http-server

WORKDIR /app

RUN cd /app && mkdir dist
COPY ./dist /app/dist

EXPOSE 8080
CMD http-server dist