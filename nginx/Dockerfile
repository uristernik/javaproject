FROM nginx:alpine
 
 # Create directories for static files and custom error pages
 RUN mkdir -p /etc/nginx/html/static/errors
 
 # Copy the custom error pages
 COPY 404.html /etc/nginx/html/static/errors/
 COPY 500.html /etc/nginx/html/static/errors/
 
 # Copy the Nginx configuration
 COPY nginx.conf /etc/nginx/nginx.conf
 
 EXPOSE 80
 
 CMD ["nginx", "-g", "daemon off;"]
