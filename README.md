# Patient Service – local setup

## Get it started
> cp .env.example .env

## 🔌 Kafka connection

👉 **Dev (local)**  
Use in case connecting from host (`localhost`) by port **9094**:


👉 **Stage / Prod (docker network)**  
Services are communicating inside internal pre-created 🐳 Docker network by hostname **kafka** and port **9092**:

## Services Start

create network external network (one-time use only)

TODO: by script ansible/terraform/init.sh

```bash
docker network create --driver bridge patient-network
````

### dev
```bash
docker compose -f ./docker-compose.dev.yml up -d
```
```bash
docker compose -f ./docker-compose.dev.yml -f infra/docker-compose.kafka.yml up -d
```
