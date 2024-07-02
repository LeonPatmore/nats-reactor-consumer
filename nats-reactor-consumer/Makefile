docker:
	gradlew jibBuildTar
	docker image load -i .\nats-test-consumer\build\jib-image.tar

docker-run:
	docker rm -f nats-test-consumer || true
	docker run -d -e NATS_CONSUMER_HOST=nats -e NATS_TEST_PROCESSOR_JSON_SERVER_HOST=jsonserver \
 		-e NATS_TEST_PROCESSOR_JSON_SERVER_PORT=3000 --name nats-test-consumer  \
 		--network local_nats_net nats-test-consumer:latest

docker-logs:
	docker logs -f nats-test-consumer
