build:
	docker build -t nats-performance-test .

run:
	docker run --network local_nats_net -v .:/app --rm nats-performance-test run /app/test.js
