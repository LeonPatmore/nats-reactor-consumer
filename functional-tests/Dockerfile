# This should be built from the root of the project.
FROM python:3.12

WORKDIR /jdk
RUN wget -O - https://apt.corretto.aws/corretto.key | gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg
RUN echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list
RUN apt-get update
RUN apt-get install -y java-21-amazon-corretto-jdk
ENV DYNACONF_JAVA_HOME="/usr/lib/jvm/java-21-amazon-corretto"

WORKDIR /app/nats-reactor-consumer
COPY ./nats-reactor-consumer .
RUN ./gradlew init
RUN ./gradlew build -x test

WORKDIR /app/functional-tests
RUN pip install pipenv
COPY ./functional-tests/Pipfile .
COPY ./functional-tests/Pipfile.lock .
RUN pipenv install

COPY ./functional-tests/pytest.ini .
COPY ./functional-tests/utils utils
COPY ./functional-tests/test_basic_consuming.py .

ENV DYNACONF_NATS_HOST="nats"
ENV DYNACONF_JSON_SERVER_HOST="jsonserver"
ENV DYNACONF_JSON_SERVER_PORT="3000"
ENV NATS_CONSUMER_HOST="nats"
ENV NATS_TEST_PROCESSOR_JSON_SERVER_HOST="jsonserver"
ENV NATS_TEST_PROCESSOR_JSON_SERVER_PORT="3000"

CMD [ "pipenv", "run", "pytest" ]
