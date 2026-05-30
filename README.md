Бэкенд для веб-приложения блога.
Задание по курсу Яндекс.Практикум "Middle Java разработчик"

Сборка проекта: `mvn clean package`
Запуск: положить .war в /webapps запущенного Tomcat

Запуск БД из корня проекта: `docker run --name yp-database --rm --env-file env/.env -v postgres_data:/var/lib/postgresql/data postgres:16`
