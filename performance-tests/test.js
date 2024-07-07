import { Nats } from "k6/x/nats";
import http from "k6/http";
import { sleep } from "k6";
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";
import { Gauge } from "k6/metrics";

const missingProcessed = new Gauge("missing_processed");

const natsConfig = {
  servers: ["nats://nats:4222"],
  unsafe: true,
};
const nats = new Nats(natsConfig);

function getCount() {
  const response = http.get(
    "http://jsonserver:3000/messages?_page=1&_per_page=1"
  );
  return response.json().items;
}

export function setup() {
  const initialCount = getCount();
  console.log(`Initial count is ${initialCount}`);
  return { initialCount };
}

export function teardown(data) {
  console.log("Teardown started, waiting 5s");
  sleep(5);
  const finalCount = getCount();
  const increase = finalCount - data.initialCount;
  const expectedCount = durationSeconds * rps + 1;
  const missing = expectedCount - increase;
  console.log(
    `Final count is ${finalCount}, so an increase of ${increase} / ${expectedCount} with missing ${missing}`
  );
  missingProcessed.add(missing);
}

export default function () {
  nats.publishWithHeaders("nats.test", "data", { id: Date.now().toString(36) });
}

export function handleSummary(data) {
  return {
    "/app/result.html": htmlReport(data),
    stdout: textSummary(data, { indent: " ", enableColors: true }),
  };
}

const durationSeconds = 120;
const rps = 100;

export const options = {
  thresholds: {
    missing_processed: ["value <= 0"],
  },
  scenarios: {
    constant_request_rate: {
      executor: "constant-arrival-rate",
      rate: rps,
      timeUnit: "1s", // 1000 iterations per second, i.e. 1000 RPS
      duration: `${durationSeconds}s`,
      preAllocatedVUs: 100, // how large the initial pool of VUs would be
      maxVUs: 200, // if the preAllocatedVUs are not enough, we can initialize more
    },
  },
};
