FROM nginx:alpine

WORKDIR /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY . .

EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
