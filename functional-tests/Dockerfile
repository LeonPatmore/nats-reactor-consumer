FROM python:3.12

WORKDIR /app
RUN pip install pipenv
COPY Pipfile .
COPY Pipfile.lock .
RUN pipenv install

COPY pytest.ini .
COPY utils utils
COPY test_basic_consuming.py .

ENV DYNACONF_NATS_HOST="nats"
ENV DYNACONF_JSON_SERVER_HOST="jsonserver"
ENV DYNACONF_JSON_SERVER_PORT="3000"

CMD [ "pipenv", "run", "pytest" ]
