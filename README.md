# Restful Booker Automation Project

A small test automation framework for API and browser testing using:

- ✅ REST Assured (API)
- ✅ Playwright for Java (UI)
- ✅ TestNG
- ✅ Maven

---

## ✅ Prerequisites

- Java 17+ (Java 22 recommended)
- Maven
- Node.js (for Playwright installation)
- [Playwright for Java setup](https://playwright.dev/java/docs/intro)

### ▶️ Run Tests

Run all tests:
./run-tests.sh      # Mac/Linux
run-tests.bat       # Windows

Run only API tests:
./run-tests.sh api

Run only UI tests:
./run-tests.sh ui

### Install Playwright CLI:

```bash
npm i -g playwright
playwright install