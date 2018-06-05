1)sudo ./mvnw package -Pprod dockerfile:build -DskipTests -e //собрать проект
2)sudo docker-compose -f src/main/docker/app.yml up //Запустить 
***требуется Docker & Docker Compose***
