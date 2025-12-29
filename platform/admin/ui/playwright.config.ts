import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright configuration for DDF Admin Console E2E tests.
 *
 * These tests replace the legacy CasperJS tests for:
 * - Installer wizard navigation (pageSelection)
 * - Configuration form validation (configuration)
 * - Configuration module (configurations)
 *
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
    // Base URL for the DDF Admin Console
    // The admin installer runs on port 8383 during initial setup
    baseURL: process.env.ADMIN_URL || 'http://localhost:8383',

    // Collect trace when retrying failed tests
    trace: 'on-first-retry',

    // Screenshot on failure
    screenshot: 'only-on-failure',

    // Viewport size matching the original CasperJS tests
    viewport: { width: 2452, height: 868 },
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
});
