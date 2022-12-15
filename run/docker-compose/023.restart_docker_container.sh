docker-compose up -d --scale $1=0
echo ''
docker-compose up -d --scale $1=1