FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install --silent
COPY . .
RUN npm run build

FROM nginx:stable-alpine as production-stage
COPY docker/nginx/nginx.conf /etc/nginx
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
