import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright configuration for DDF Search UI E2E tests.
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['html', { open: 'never' }],
    ['list'],
  ],
  use: {
    // Base URL for the DDF instance
    baseURL: process.env.DDF_URL || 'https://localhost:8993',

    // Ignore HTTPS errors for self-signed certificates
    ignoreHTTPSErrors: true,

    // Collect trace when retrying failed tests
    trace: 'on-first-retry',

    // Screenshot on failure
    screenshot: 'only-on-failure',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
  ],

  // Timeout for each test
  timeout: 60000,

  // Global setup/teardown (can be used to start DDF if needed)
  // globalSetup: require.resolve('./e2e/global-setup'),
});
