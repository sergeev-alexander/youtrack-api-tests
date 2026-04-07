# YouTrack API Test Framework

## Stack

- Java 24
- Gradle 9
- TestNG
- RestAssured
- Hamcrest
- Jackson
- Logback
- Docker (YouTrack)

## Run

### 1. Run Docker

```bash
cd docker
docker compose up -d
```

### 2. Wait until `docker ps` shows `healthy` (~2 min.)

### 3. Get permanent token

1. Open http://localhost:8080
2. Create admin account
3. Generate permanent token (Admin -> Profile -> Security -> New Token) 

### 4. Set permanent token as env variable

Must begin with `perm:` not with `perm-` (replace `-` to `:`)

```bash
# Windows PowerShell
$env:YOUTRACK_AUTH_TOKEN = "permanent_token"

# Linux
export YOUTRACK_AUTH_TOKEN="permanent_token"
```

### 5. Run tests

```bash
# All tests
./gradlew clean test

# Positive tests
./gradlew clean test -Dgroups=positive

# Negative tests
./gradlew clean test -Dgroups=negative
```

## Report 

- `build/reports/tests/test/index.html`

## Logs

- `build/logs/app.log` - framework logs
- `build/logs/rest-assured.log` - Request / Response (if test fails)