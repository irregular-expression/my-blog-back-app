Бэкенд для веб-приложения блога.
Задание по курсу Яндекс.Практикум "Middle Java разработчик"

Сборка проекта:
```bash
./gradlew clean build
```

Запуск: из IDE, либо 
```bash
java -jar build/libs/my-blog-back-app-0.0.1-SNAPSHOT.jar --server.port=8081
```

Запуск БД из корня проекта: 
```bash
docker run --name yp-database --rm --env-file env/.env -v postgres_data:/var/lib/postgresql/data -p 5433:5432 postgres:16
```

Вместо env/.env подкладываем путь до своего файла с настройками БД вида:
`POSTGRES_USER=user
POSTGRES_PASSWORD=password`

Накат БД выполняется автоматически при сборке проекта. Тестовая БД накатывается при прогоне интеграционных тестов.