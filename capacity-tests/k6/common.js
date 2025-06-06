// Test profiles
export const testProfiles = {
  smoke: {
    options: {
      vus: 5,
      duration: '10s',
    },
    exec: 'smokeTest',
  },
  load: {
    options: {
      stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 0 },
      ],
    },
    exec: 'loadTest',
  },
  stress: {
    options: {
      stages: [
        { duration: '30s', target: 120 },
        { duration: '1m', target: 120 },
        { duration: '30s', target: 0 },
      ],
    },
    exec: 'stressTest',
  },
  soak: {
    options: {
      stages: [
        { duration: '2m', target: 50 },
        { duration: '5m', target: 50 },
        { duration: '2m', target: 0 },
      ],
    },
    exec: 'soakTest',
  },
  spike: {
    options: {
      stages: [
        { duration: '10s', target: 20 },
        { duration: '10s', target: 200 },
        { duration: '10s', target: 20 },
        { duration: '10s', target: 0 },
      ],
    },
    exec: 'spikeTest',
  },
  breakpoint: {
    options: {
      stages: [
        { duration: '15s', target: 50 },
        { duration: '5s', target: 50 },
        { duration: '15s', target: 100 },
        { duration: '5s', target: 100 },
        { duration: '15s', target: 200 },
        { duration: '5s', target: 200 },
        { duration: '15s', target: 400 },
        { duration: '5s', target: 400 },
        { duration: '15s', target: 800 },
        { duration: '5s', target: 800 },
        { duration: '15s', target: 1200 },
        { duration: '5s', target: 1200 },
        { duration: '15s', target: 0 },
      ],
    },
    exec: 'breakpointTest',
  },
};

export const commonThresholds = {
  'http_req_duration': ['p(95)<400'],
  'http_req_failed': ['rate<0.01'],
  'checks': ['rate>0.99'],
};

// more strict thresholds on aborts
export const breakpointThresholds = {
  'http_req_duration': [
    { threshold: 'p(95)<500', abortOnFail: true, delayAbortEval: '30s' },
  ],
  'http_req_failed': [
    { threshold: 'rate<0.05', abortOnFail: true, delayAbortEval: '30s' },
  ],
  'checks': [
    { threshold: 'rate>0.95', abortOnFail: true, delayAbortEval: '30s' },
  ],
};

// generate scenarios dynamically from the profile
export function generateScenarios(selectedProfile = 'smoke') {
  const selected = testProfiles[selectedProfile];

  if (!selected) {
    throw new Error(`Invalid test profile: ${selectedProfile}`);
  }

  return {
    [selectedProfile]: {
      executor: selected.options.stages ? 'ramping-vus' : 'constant-vus',
      ...selected.options,
      exec: selected.exec,
    },
  };
}
