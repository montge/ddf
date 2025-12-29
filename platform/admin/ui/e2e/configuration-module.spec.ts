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
 * Migrated from CasperJS: admin-modules-configuration/src/test/js/configurations.js
 */
import { test, expect } from '@playwright/test';

/**
 * DDF Admin Configuration Module Tests
 *
 * Tests the configuration module including:
 * - Presence of configuration containers
 * - Platform Global Configuration form interaction
 * - Form field population and submission
 *
 * Prerequisites:
 * - DDF Admin Console running at http://localhost:8383 (or ADMIN_URL env var)
 */
test.describe('Configuration Module', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test.describe('Configuration Containers', () => {
    test('should have configurations container', async ({ page }) => {
      await expect(page.locator('#configurations')).toBeVisible();
    });

    test('should have servicesRegion container', async ({ page }) => {
      await expect(page.locator('#servicesRegion')).toBeVisible();
    });

    test('should have ddfplatformconfig container', async ({ page }) => {
      await expect(page.locator('#ddfplatformconfig')).toBeVisible();
    });
  });

  test.describe('Platform Global Configuration', () => {
    test('should open and close Platform Global Configuration', async ({ page }) => {
      // Click on Platform Global Configuration link
      const configLink = page.getByText('Platform Global Configuration', { exact: true });
      await expect(configLink).toBeVisible();
      await configLink.click();

      // Verify cancel button exists and click it
      const cancelButton = page.locator('.btn.btn-default.cancel-button');
      await expect(cancelButton).toBeVisible();
      await cancelButton.click();
    });

    test('should open ddf.platform.config form', async ({ page }) => {
      // Click on ddf.platform.config link
      const platformConfigLink = page.getByText('ddf.platform.config', { exact: true });
      await expect(platformConfigLink).toBeVisible();
      await platformConfigLink.click();

      // Verify the form exists
      const configForm = page.locator('#ddfplatformconfig form.add-federated-source');
      await expect(configForm).toBeVisible();
    });

    test('should fill and submit Platform Global Configuration form', async ({ page }) => {
      // Click on ddf.platform.config link
      const platformConfigLink = page.getByText('ddf.platform.config', { exact: true });
      await expect(platformConfigLink).toBeVisible();
      await platformConfigLink.click();

      // Wait for form
      const configForm = page.locator('#ddfplatformconfig form.add-federated-source');
      await expect(configForm).toBeVisible();

      // Fill form fields
      await page.fill('#ddfplatformconfig .add-federated-source input[name="protocol"]', 'http');
      await page.fill('#ddfplatformconfig .add-federated-source input[name="host"]', 'localhost');
      await page.fill('#ddfplatformconfig .add-federated-source input[name="port"]', '8181');
      await page.fill('#ddfplatformconfig .add-federated-source input[name="trustStore"]', '');
      await page.fill(
        '#ddfplatformconfig .add-federated-source input[name="trustStorePassword"]',
        ''
      );
      await page.fill('#ddfplatformconfig .add-federated-source input[name="keyStore"]', '');
      await page.fill(
        '#ddfplatformconfig .add-federated-source input[name="keyStorePassword"]',
        ''
      );
      await page.fill(
        '#ddfplatformconfig .add-federated-source input[name="id"]',
        'ddf.distribution'
      );
      await page.fill('#ddfplatformconfig .add-federated-source input[name="version"]', '2.3.0');
      await page.fill(
        '#ddfplatformconfig .add-federated-source input[name="organization"]',
        'Codice Foundation'
      );

      // Click submit button
      const submitButton = page.locator('.btn.btn-primary.submit-button');
      await expect(submitButton).toBeVisible();
      await submitButton.click();
    });
  });
});
