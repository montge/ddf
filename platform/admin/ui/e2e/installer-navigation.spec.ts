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
 * Migrated from CasperJS: admin-modules-installer/src/test/js/pageSelection.js
 */
import { test, expect } from '@playwright/test';

/**
 * DDF Admin Installer - Page Selection Tests
 *
 * Tests the installer wizard navigation including:
 * - Presence of all wizard step containers
 * - Navigation button functionality (start, next, previous, finish)
 *
 * Prerequisites:
 * - DDF Admin Console running at http://localhost:8383 (or ADMIN_URL env var)
 */
test.describe('Installer Page Selection', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test.describe('Wizard Step Containers', () => {
    test('should have Welcome container', async ({ page }) => {
      await expect(page.locator('#welcome')).toBeVisible();
    });

    test('should have Configuration container', async ({ page }) => {
      await expect(page.locator('#configuration')).toBeVisible();
    });

    test('should have Applications container', async ({ page }) => {
      await expect(page.locator('#applications')).toBeVisible();
    });

    test('should have Finish container', async ({ page }) => {
      await expect(page.locator('#finish')).toBeVisible();
    });

    test('should have Navigation container', async ({ page }) => {
      await expect(page.locator('#navigation')).toBeVisible();
    });
  });

  test.describe('Navigation Buttons', () => {
    test('should navigate through wizard steps', async ({ page }) => {
      // Click Start button
      const startButton = page.locator('#startStep');
      await expect(startButton).toBeVisible();
      await startButton.click();

      // Click Previous button
      const previousButton = page.locator('#previousStep');
      await expect(previousButton).toBeVisible();
      await previousButton.click();

      // Click Start again
      await expect(startButton).toBeVisible();
      await startButton.click();

      // Click Next button
      const nextButton = page.locator('#nextStep');
      await expect(nextButton).toBeVisible();
      await nextButton.click();

      // Click Previous again
      await expect(previousButton).toBeVisible();
      await previousButton.click();

      // Click Next twice
      await expect(nextButton).toBeVisible();
      await nextButton.click();
      await expect(nextButton).toBeVisible();
      await nextButton.click();

      // Click Finish button
      const finishButton = page.locator('#finishStep');
      await expect(finishButton).toBeVisible();
      await finishButton.click();
    });
  });
});
