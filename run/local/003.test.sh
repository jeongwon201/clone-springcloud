echo "product create"
curl --header "Content-Type: application/json" \
--request POST \
--data '{"productId":9,"productName":"9_name","productInfo":"9_productInfo","recommendList":[{"recommendId":91,"author":"Author91","content":"Content91"}],"reviewList":[{"reviewId":1,"author":"Author1","subject":"Subject1","content":"Content1"}]}' \
http://localhost:7000/composite

echo "product select"
curl http://localhost:7000/composite/9 | jq

echo "product delete"
curl -X "DELETE" http://localhost:7000/composite/9