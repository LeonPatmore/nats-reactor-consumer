# NATS Reactor Consumer

A reactor library for consuming from NATS.

- [Consumer Client](nats-reactor-consumer/nats-reactive-consumer): A generic consuming client.
- [Test Consumer](nats-reactor-consumer/nats-test-processor): An example of a SpringBoot service using the client. Used for functional tests.

## Functional Tests

- `test_simple_message_is_processed`: Ensures a message is consumed and acked.
- `test_message_that_errors_goes_to_dlq`: A message that continuously errors will go to a DLQ.
- `test_message_that_does_not_ack_in_time_goes_to_dlq`: A message that does not ack in time will eventually go to the DLQ.
- `test_message_when_service_dies_it_is_retried`: When the consumer dies during processing of a message, this message is retried.

## Local Testing

1. Start NATS locally: `cd deployments/local && make start`
2. Build the tests: `cd functional-tests && make build`
3. Run the tests: `cd functional-tests && make run`

## Monitoring

Monitoring: https://natsdashboard.com/jetstream?url=http%3A%2F%2Flocalhost%3A8222

## Documentation

- NATS consumer properties: https://docs.nats.io/nats-concepts/jetstream/consumers
- NATS examples: https://github.com/synadia-io/rethink_connectivity
- NATS monitoring: https://natsdashboard.com/?url=http%3A%2F%2Flocalhost%3A8222
