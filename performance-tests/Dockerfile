FROM grafana/xk6 as k6_builder
RUN xk6 build --with github.com/ydarias/xk6-nats@latest

FROM alpine:latest
COPY --from=k6_builder /xk6/k6 /usr/bin/k6
ENTRYPOINT ["k6"]
