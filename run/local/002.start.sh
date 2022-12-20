cd ../..

gradle build

java -jar ./cloud/eureka/build/libs/eureka-1.0.jar &

java -jar ./services/composite/build/libs/composite-1.0.jar &

java -jar ./services/product/build/libs/product-1.0.jar &

java -jar ./services/recommend/build/libs/recommend-1.0.jar &

java -jar ./services/review/build/libs/review-1.0.jar &