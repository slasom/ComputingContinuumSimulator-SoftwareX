# Use Node 16 alpine as parent image
FROM node:16-alpine

# Create app directory
WORKDIR /usr/src/app

# Copy application files to the container
COPY app.js .

# Expose application port
EXPOSE 8080

# Start the application
CMD ["node", "app.js"]
