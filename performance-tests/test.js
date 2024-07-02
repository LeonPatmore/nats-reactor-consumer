import { Nats } from "k6/x/nats";

const natsConfig = {
  servers: ["nats://nats:4222"],
  unsafe: true,
};
const nats = new Nats(natsConfig);

export default function () {
  nats.publishWithHeaders("nats.test", "data", { id: Date.now().toString(36) });
}

export const options = {
  scenarios: {
    constant_request_rate: {
      executor: "constant-arrival-rate",
      rate: 5,
      timeUnit: "1s", // 1000 iterations per second, i.e. 1000 RPS
      duration: "120s",
      preAllocatedVUs: 100, // how large the initial pool of VUs would be
      maxVUs: 200, // if the preAllocatedVUs are not enough, we can initialize more
    },
  },
};
