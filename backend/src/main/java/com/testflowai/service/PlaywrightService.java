package com.testflowai.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.testflowai.entity.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

/**
 * Playwright 浏览器自动化服务
 * @author TestFlowAI
 */
@Service
public class PlaywrightService {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightService.class);

    @Value("${playwright.headless:true}")
    private boolean headless;

    @Value("${playwright.timeout:30000}")
    private int timeout;

    @Value("${playwright.screenshot-path:/tmp/testflowai/screenshots}")
    private String screenshotPath;

    private Browser browser;
    private BrowserContext context;
    private Page page;

    /**
     * 启动浏览器
     */
    public void launchBrowser() {
        try {
            Playwright playwright = Playwright.create();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setTimeout(timeout);

            browser = playwright.chromium().launch(launchOptions);

            context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080));

            page = context.newPage();

            log.info("浏览器启动成功，headless={}", headless);
        } catch (Exception e) {
            log.error("浏览器启动失败：{}", e.getMessage(), e);
            throw new RuntimeException("浏览器启动失败：" + e.getMessage(), e);
        }
    }

    /**
     * 关闭浏览器
     */
    public void closeBrowser() {
        try {
            if (context != null) {
                context.close();
            }
            if (browser != null) {
                browser.close();
            }
            log.info("浏览器已关闭");
        } catch (Exception e) {
            log.error("关闭浏览器失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 执行测试步骤
     */
    public StepResult executeStep(Map<String, Object> step, String executionId) {
        StepResult result = new StepResult();
        result.setResultId(UUID.randomUUID().toString());
        result.setStartTime(new Date());

        try {
            String type = (String) step.get("type");
            String selector = (String) step.get("selector");
            String value = (String) step.get("value");
            String description = (String) step.get("description");

            log.info("执行步骤：{} - {}", type, description);

            switch (type) {
                case "click":
                    handleClick(selector);
                    break;
                case "dblclick":
                    handleDblClick(selector);
                    break;
                case "input":
                    handleInput(selector, value);
                    break;
                case "select":
                    handleSelect(selector, value);
                    break;
                case "check":
                    handleCheck(selector, true);
                    break;
                case "uncheck":
                    handleCheck(selector, false);
                    break;
                case "navigate":
                    handleNavigate(value);
                    break;
                case "scroll":
                    handleScroll(selector, value);
                    break;
                case "hover":
                    handleHover(selector);
                    break;
                case "keypress":
                    handleKeypress(selector, value);
                    break;
                case "assert":
                    handleAssert(selector, value);
                    break;
                default:
                    throw new RuntimeException("不支持的操作类型：" + type);
            }

            result.setStatus("passed");
            result.setLog("步骤执行成功：" + description);

        } catch (Exception e) {
            log.error("步骤执行失败：{}", e.getMessage(), e);
            result.setStatus("failed");
            result.setErrorMessage(e.getMessage());
        }

        result.setEndTime(new Date());
        return result;
    }

    private void handleClick(String selector) {
        page.locator(selector).click();
        waitForPageLoad();
    }

    private void handleDblClick(String selector) {
        page.locator(selector).dblclick();
        waitForPageLoad();
    }

    private void handleInput(String selector, String value) {
        page.locator(selector).fill(value);
    }

    private void handleSelect(String selector, String value) {
        page.locator(selector).selectOption(value);
    }

    private void handleCheck(String selector, boolean checked) {
        Locator locator = page.locator(selector);
        if (checked) {
            locator.check();
        } else {
            locator.uncheck();
        }
    }

    private void handleNavigate(String url) {
        page.navigate(url);
        waitForPageLoad();
    }

    private void handleScroll(String selector, String value) {
        if (selector != null && !selector.isEmpty()) {
            page.locator(selector).scrollIntoViewIfNeeded();
        }
    }

    private void handleHover(String selector) {
        page.locator(selector).hover();
    }

    private void handleKeypress(String selector, String key) {
        page.locator(selector).press(key);
    }

    private void handleAssert(String selector, String expectedValue) {
        Locator locator = page.locator(selector);
        String actualValue = locator.textContent();
        if (!actualValue.contains(expectedValue)) {
            throw new AssertionError(String.format("断言失败：期望包含 '%s'，实际为 '%s'", expectedValue, actualValue));
        }
    }

    private void waitForPageLoad() {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(10000));
    }

    /**
     * 截图
     */
    public String takeScreenshot(String stepId) {
        try {
            Path dir = Paths.get(screenshotPath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String filename = String.format("step_%s_%d.png", stepId, System.currentTimeMillis());
            Path filepath = dir.resolve(filename);

            page.screenshot(new Page.ScreenshotOptions().setPath(filepath));

            log.info("截图已保存：{}", filepath);
            return filepath.toString();
        } catch (Exception e) {
            log.error("截图失败：{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取当前页面 URL
     */
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * 获取页面标题
     */
    public String getPageTitle() {
        return page.title();
    }

    /**
     * 执行 JavaScript
     */
    public Object executeScript(String script) {
        return page.evaluate(script);
    }

    /**
     * 等待元素出现
     */
    public boolean waitForElement(String selector, int timeout) {
        try {
            page.locator(selector).waitFor(new Locator.WaitForOptions().setTimeout(timeout));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
