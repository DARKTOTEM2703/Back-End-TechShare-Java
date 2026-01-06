Backend changes committed (English summary)
=========================================

- Implemented `MaterialManagementUseCaseImpl` to satisfy dependency injection and provide material creation/listing use cases.
- Ensured S3/MinIO storage configuration and `S3ImageStorage` integration work when backend runs on host (updated `.env` recommended for host: `MINIO_ENDPOINT=http://localhost:9000`, `REDIS_HOST=localhost`).
- Fixed wiring for use cases and beans so the application starts with storages in Docker and the backend on host.
- Added small logging and health-check observations; backend `actuator/health` returns UP when MinIO/Redis reachable.

Files changed: `core/application/usecase/MaterialManagementUseCaseImpl.java`, config and env adjustments, logging observations.

How to run locally (host backend, storages in Docker):

1. Start Postgres/Redis/MinIO via docker-compose (from repo root)
2. Ensure `.env` has MINIO and REDIS set to `localhost` when running backend on host
3. cd Back-End-TechShare-Java
4. mvn -DskipTests spring-boot:run
