# Ingest API

Java 21 / Spring Boot 3 REST service for a 3-hour practice drill: ingest events, persist to Postgres, deploy to DigitalOcean.

## Run locally

Env vars:

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `8080` | HTTP port |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/ingest` | Postgres URL (`jdbc:` or `postgresql://user:pass@host:5432/db`) |
| `DATABASE_USER` / `DATABASE_PASSWORD` | `ingest` | Used when `DATABASE_URL` has no credentials |
| `LOG_LEVEL` | `INFO` | Log level |
| `X-Request-Id` | generated | Echoed on every response and in logs |

```bash
docker compose up --build
curl http://localhost:8080/health
curl http://localhost:8080/ready
```

```bash
curl -s -D - -X POST http://localhost:8080/v1/events \
  -H "Content-Type: application/json" \
  -d '{"eventId":"e-100","type":"click","occurredAt":"2026-10-21T11:00:00Z","value":1.5}'
```

New event: `201`. Same `eventId` again: `200` and the original row.

## Tests

```bash
mvn test
```

## DigitalOcean App Platform

This is the practice deploy path: Docker image + managed Postgres + HTTP health check.

1. Create a DigitalOcean account and use the provided credits.
2. Push this repo to GitHub (or upload the Dockerfile from the App Platform UI).
3. **Create App** → **Dockerfile** in the repo root. HTTP port `8080`.
4. Add a **Postgres 16** database component (dev size is enough).
5. Bind it to the API service and set:

   - `PORT=8080`
   - `LOG_LEVEL=INFO`
   - `DATABASE_URL` = the database's connection string (App Platform can inject `${db.DATABASE_URL}` — see [`.do/app.yaml`](.do/app.yaml))

6. Health check path: `/health` (liveness, no DB). After deploy, confirm `/ready` is `200` so Postgres is reachable.
7. Deploy. Prove it from the public URL:

```bash
curl https://YOUR_APP.ondigitalocean.app/health
curl https://YOUR_APP.ondigitalocean.app/ready
curl -s -D - -X POST https://YOUR_APP.ondigitalocean.app/v1/events \
  -H "Content-Type: application/json" \
  -d '{"eventId":"e-100","type":"click","occurredAt":"2026-10-21T11:00:00Z","value":1.5}'
```

With `doctl` logged in:

```bash
doctl apps create --spec .do/app.yaml
doctl apps list
```

If `DATABASE_URL` uses `sslmode=require` (managed Postgres), the JDBC parser keeps the query string.

Do not point the load balancer health check at `/ready` if you want the container to stay up while Postgres is still provisioning; use `/health` for that, then verify `/ready` yourself.
