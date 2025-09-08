# Patient Service – local setup

## Start

### dev
```bash
docker compose up -d
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

### prod
```bash
docker compose -f ./docker-compose.prod.yml stop 
```