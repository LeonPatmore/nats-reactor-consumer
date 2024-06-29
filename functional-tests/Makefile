lock:
	pipenv lock
	pipenv install

build:
	cd .. && docker build -t functional-tests -f .\functional-tests\Dockerfile .

run:
	docker run --network local_nats_net --rm functional-tests

debug:
	docker run --network local_nats_net --entrypoint /bin/bash -it --rm functional-tests
