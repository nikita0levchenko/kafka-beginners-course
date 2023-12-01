# kafka-beginners-course
## setup your docker container with kafka cluster
```
curl -L https://releases.conduktor.io/quick-start -o docker-compose.yml && docker compose up -d --wait && echo "Conduktor started on http://localhost:8080"
```
## create topic
```
kafka-topics --bootstrap-server localhost:19092 --create --topic scala-demo --partitions 3
```
## describe topic
```
kafka-topics --bootstrap-server localhost:19092 --topic scala-demo --describe
```
