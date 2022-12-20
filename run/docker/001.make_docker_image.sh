cd ../ ..
project_dir=$(pwd)

cd $project_dir
gradle build

cd $project_dir/cloud/eureka
docker build -t eureka -f ./Dockerfile .

cd $project_dir/services/composite
docker build -t composite -f ./Dockerfile .

cd $project_dir/services/product
docker build -t product -f ./Dockerfile .

cd $project_dir/services/recommend
docker build -t recommend -f ./Dockerfile .

cd $project_dir/services/review
docker build -t review -f ./Dockerfile .