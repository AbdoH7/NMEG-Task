FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN addgroup --system --gid 1001 app
RUN adduser --system --uid 1001 app-user

RUN chown -R app-user:app /app

USER app-user

EXPOSE 3000

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
