# Patient Service – local setup

## Start

### dev
```bash
docker compose up -d
```
### staging
```bash
docker compose -f ./docker-compose.staging.yml up -d
```
### prod
```bash
docker compose -f ./docker-compose.prod.yml up -d
```

## Stop

### dev
```bash
docker compose stop
```
### staging
```bash
docker compose -f ./docker-compose.staging.yml stop 
```
### prod
```bash
docker compose -f ./docker-compose.prod.yml stop 
```