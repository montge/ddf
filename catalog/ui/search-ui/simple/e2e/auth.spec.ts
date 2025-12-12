import { test, expect } from '@playwright/test';

/**
 * DDF Authentication E2E Tests
 *
 * These tests verify authentication flows work correctly.
 * They require a running DDF instance with authentication configured.
 */

test.describe('Authentication', () => {
  test('should redirect to login for protected resources', async ({ page }) => {
    // Attempt to access a protected resource
    await page.goto('/admin/');

    // Should be redirected to login or show login form
    // The exact behavior depends on the authentication configuration
    await page.waitForLoadState('networkidle');

    // Check we're either on login page or see login prompt
    const url = page.url();
    const hasLoginIndicator =
      url.includes('login') ||
      url.includes('idp') ||
      await page.locator('form[action*="login"], input[type="password"]').count() > 0;

    expect(hasLoginIndicator).toBeTruthy();
  });

  test('should display logout option when authenticated', async ({ page }) => {
    // This test would need authentication setup
    // Skipping by default until auth fixtures are configured
    test.skip(true, 'Requires authentication fixtures');

    // Navigate to authenticated area
    await page.goto('/admin/');

    // Look for logout link/button
    const logoutElement = page.locator('text=Logout, text=Sign Out, [href*="logout"]');
    await expect(logoutElement).toBeVisible();
  });
});

test.describe('Guest Access', () => {
  test('should allow guest access to search if configured', async ({ page }) => {
    // Navigate to search - should work for guests
    const response = await page.goto('/search/simple/');

    // Check we got a successful response (not 401/403)
    expect(response?.status()).toBeLessThan(400);
  });
});
