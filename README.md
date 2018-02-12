# messaging
Simple programs demonstrating pub-sub messaging with various message brokers

## Docker commands
cd messating/src/main/scala/com/messaging/activemq
docker build -t artemis-messaging .
docker run -p 5672:5672 --rm -it artemis-messaging

docker run -p 6379:6379 --rm --name redis-server redis:alpine --appendonly yes

docker run --rm -p 5672:5672 rabbitmq:alpine
