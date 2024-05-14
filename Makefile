start:
	docker-compose -f .\nats-compose.yaml up -d

restart:
	docker-compose -f .\nats-compose.yaml restart
