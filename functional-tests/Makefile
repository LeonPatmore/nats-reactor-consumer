lock:
	pipenv lock
	pipenv install

build:
	docker build -t functional-tests .

run:
	docker run -v /var/run/docker.sock:/var/run/docker.sock --network local_nats_net --rm functional-tests

debug:
	docker run -v /var/run/docker.sock:/var/run/docker.sock --network local_nats_net --entrypoint /bin/bash -it --rm functional-tests
