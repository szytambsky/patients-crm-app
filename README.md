# Patient Service – local setup

## 🔌 Kafka connection

👉 **Dev (local)**  
Use in case connecting from host (`localhost`) by port **9094**:


👉 **Stage / Prod (docker network)**  
Services are communicating inside internal pre-created 🐳 Docker network by hostname **kafka** and port **9092**:


## Services Start
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