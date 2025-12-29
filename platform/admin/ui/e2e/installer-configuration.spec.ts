/**
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Migrated from CasperJS: admin-modules-installer/src/test/js/configuration.js
 */
import { test, expect } from '@playwright/test';

/**
 * DDF Admin Installer - Configuration View Tests
 *
 * Tests the configuration form validation including:
 * - Form element presence
 * - Hostname validation (empty not allowed)
 * - Port validation (non-numeric not allowed, empty not allowed)
 * - Successful form submission with valid values
 *
 * Prerequisites:
 * - DDF Admin Console running at http://localhost:8383 (or ADMIN_URL env var)
 */
test.describe('Installer Configuration View', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should have welcome and configuration containers', async ({ page }) => {
    await expect(page.locator('#welcome')).toBeVisible();
    await expect(page.locator('#configuration')).toBeVisible();
  });

  test.describe('Configuration Form', () => {
    test.beforeEach(async ({ page }) => {
      // Navigate to configuration step
      const startButton = page.locator('#startStep');
      await expect(startButton).toBeVisible();
      await startButton.click();

      // Wait for config form
      await expect(page.locator('#config-form')).toBeVisible();
    });

    test('should not submit with empty hostname', async ({ page }) => {
      // Clear hostname field
      await page.fill('input[name="host"]', '');

      // Click next
      const nextButton = page.locator('#nextStep');
      await nextButton.click();

      // Should still be on config form (validation failed)
      await expect(page.locator('#config-form')).toBeVisible();
    });

    test('should not submit with invalid port value', async ({ page }) => {
      // Fill valid hostname
      await page.fill('input[name="host"]', 'localhost');

      // Fill invalid port (non-numeric)
      await page.fill('input[name="port"]', 'blah');

      // Click next
      const nextButton = page.locator('#nextStep');
      await nextButton.click();

      // Should still be on config form (validation failed)
      await expect(page.locator('#config-form')).toBeVisible();
    });

    test('should not submit with empty port value', async ({ page }) => {
      // Fill valid hostname
      await page.fill('input[name="host"]', 'localhost');

      // Clear port field
      await page.fill('input[name="port"]', '');

      // Click next
      const nextButton = page.locator('#nextStep');
      await nextButton.click();

      // Should still be on config form (validation failed)
      await expect(page.locator('#config-form')).toBeVisible();
    });

    test('should submit with valid hostname and port', async ({ page }) => {
      // Fill valid hostname
      await page.fill('input[name="host"]', 'localhost');

      // Fill valid port
      await page.fill('input[name="port"]', '8181');

      // Click next
      const nextButton = page.locator('#nextStep');
      await nextButton.click();

      // Config form should no longer be visible (moved to next step)
      await expect(page.locator('#config-form')).not.toBeVisible();
    });
  });
});
