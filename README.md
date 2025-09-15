# Patient Service – local setup

## Start

### dev
```bash
docker compose -f ./docker-compose.dev.yml up -d
```
### prod
```bash
docker compose -f ./docker-compose.prod.yml up -d
```

## Stop

### dev
```bash
docker compose -f ./docker-compose.dev.yml stop
```
### prod
```bash
docker compose -f ./docker-compose.prod.yml stop 
```