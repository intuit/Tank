# Pull Requests

- When discussing or preparing to create a pull request, remind the user to add the appropriate release-impact label (`major`, `minor`, or `patch`) and any other relevant topical labels.
- Suggest specific labels based on the change before the pull request is created.

# UI validation

- For local UI bugs and “is it actually working?” checks, use the Playwright MCP: navigate, snapshot/screenshot, exercise the real click path, then inspect console + network on failure.
- User screenshots are primary evidence — reproduce that state in the browser before concluding a fix.
- See `.cursor/rules/playwright-ui-validation.mdc` for the full workflow.
