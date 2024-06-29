start:
	docker-compose -f .\nats-compose.yaml up -d

restart:
	docker-compose -f .\nats-compose.yaml restart

reset:
	docker-compose -f .\nats-compose.yaml stop
	docker-compose -f .\nats-compose.yaml rm --force
	docker-compose -f .\nats-compose.yaml up -d
