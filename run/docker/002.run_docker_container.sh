sudo docker network create apps_net

sudo docker run -d --rm --name mysqlDB \
--network apps_net \
-e MYSQL_DATABASE=review-db \
-e MYSQL_USER=user01 \
-e MYSQL_PASSWORD=user01 \
-e MYSQL_ROOT_PASSWORD=password \
-p 3306:3306 \
mysql:5.7

sudo docker run -d --rm --name mongoDB \
--network apps_net \
-p 27017:27017 \
mongo:3.6.9 \
mongod --smallfiles

docker run -d --rm --name eureka \
--network apps_net \
-p 8761:8761 \
eureka

docker run -d --rm --name product \
--network apps_net \
-e SPRING_PROFILES_ACTIVE=docker \
product

docker run -d --rm --name recommend \
--network apps_net \
-e SPRING_PROFILES_ACTIVE=docker \
recommend

docker run -d --rm --name review \
--network apps_net \
-e SPRING_PROFILES_ACTIVE=docker \
review

docker run -d --rm --name composite \
--network apps_net \
-e SPRING_PROFILES_ACTIVE=docker \
composite

docker run -d --rm --name gateway \
--network apps_net \
-e SPRING_PROFILES_ACTIVE=docker \
-e SERVER_SSL_KEY_STORE=file:/keystore/gateway-docker.p12 \
-e SERVER_SSL_KEY_STORE_PASSWORD=password \
-p 443:8080 \
gateway

docker cp $PWD/keystore gateway:/keystore