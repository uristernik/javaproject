FROM nginx:alpine

 # Create directories for static files and custom error pages
 RUN mkdir -p /usr/share/nginx/html/static/errors

 # Copy the custom error pages
 COPY 404.html /usr/share/nginx/html/static/errors/
 COPY 500.html /usr/share/nginx/html/static/errors/

 # Copy the favicon
 COPY static/favicon.svg /usr/share/nginx/html/static/favicon.svg

 # Copy the Nginx configuration
 COPY nginx.conf /etc/nginx/nginx.conf

 EXPOSE 80

 CMD ["nginx", "-g", "daemon off;"]
