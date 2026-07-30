package com.intuit.tank.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Path;

/**
 * Reuses one Chromium process across UI checks; each call gets an isolated browser context.
 */
final class TankUiBrowser implements AutoCloseable {

    private final Playwright playwright;
    private final Browser browser;
    private final String baseUrl;
    private final String username;
    private final String password;

    TankUiBrowser(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("tank.ui.headless", "true"));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(headless ? 0 : 250));
    }

    void confirmJobInQueue(Path artifactDir, int jobId, String projectName, String expectedName,
                           String expectedStatus, String expectedUsers) {
        Path screenshot = artifactDir.resolve("job-queue-" + jobId + ".png");
        Path trace = artifactDir.resolve("job-queue-" + jobId + ".zip");

        BrowserContext context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        Page page = context.newPage();
        try {
            page.navigate(baseUrl + "/login.jsf");
            page.locator("#login\\:username").fill(username);
            page.locator("#login\\:password").fill(password);
            page.locator("#login\\:loginButton").click();

            page.navigate(baseUrl + "/agents/index.jsf");
            try {
                page.waitForSelector("#mainForm\\:jobTableId",
                        new Page.WaitForSelectorOptions().setTimeout(60_000));
            } catch (RuntimeException navigationFailure) {
                throw new AssertionError(
                        "Job queue unavailable after login; current URL: " + page.url(),
                        navigationFailure);
            }
            page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Refresh")).click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            Locator table = page.locator("#mainForm\\:jobTableId");
            Locator filterFinished = page.locator("#mainForm\\:filterFinishedBoxId");
            if (filterFinished.count() > 0 && filterFinished.isChecked()) {
                filterFinished.click();
                page.waitForLoadState(LoadState.NETWORKIDLE);
            }

            Locator projectRow = table.locator("tbody tr").filter(
                    new Locator.FilterOptions().setHasText(projectName)).first();
            projectRow.waitFor(new Locator.WaitForOptions().setTimeout(60_000));

            Locator collapsedToggler = projectRow.locator(
                    ".ui-treetable-toggler.ui-icon-triangle-1-e");
            if (collapsedToggler.count() > 0) {
                collapsedToggler.click();
                page.waitForLoadState(LoadState.NETWORKIDLE);
            }

            Locator jobRow = table.locator("tbody tr").filter(
                    new Locator.FilterOptions().setHasText(expectedName));
            jobRow.first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(60_000));

            String rowText = jobRow.first().innerText();
            if (!rowText.contains(String.valueOf(jobId))) {
                throw new AssertionError("Job ID should appear in the expanded job row");
            }
            if (!rowText.contains(expectedStatus)) {
                throw new AssertionError("Status " + expectedStatus + " should be visible on the job row");
            }
            if (expectedUsers != null && !rowText.contains(expectedUsers)) {
                throw new AssertionError("Configured user count should be visible on the job row");
            }

            page.screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));
        } catch (RuntimeException | AssertionError uiFailure) {
            try {
                page.screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));
            } catch (Exception ignored) {
                // best effort
            }
            throw uiFailure;
        } finally {
            context.tracing().stop(new Tracing.StopOptions().setPath(trace));
            context.close();
        }
    }

    @Override
    public void close() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
