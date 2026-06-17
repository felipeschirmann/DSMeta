# Project Guidelines - DSMeta

These are project-specific guidelines for the DSMeta repository.

## 1. Git & Workflow Authorization
- **Commit Authorization**: You MUST ask the user for explicit confirmation and authorization before executing any `git commit` command.
- **Push Authorization**: You MUST ask the user for explicit confirmation and authorization before executing any `git push` command.
- **Verification**: Ensure the application is functional, tests pass, and it compiles successfully before proposing commits.
- **Security Check**: Before proposing any commit, perform a security check to ensure no user information, local/remote configuration credentials, keys, or private variables are exposed.
- **Branching Strategy**: Always develop new features, fixes, or configuration setups on dedicated feature/bugfix branches (e.g., `feature/feature-name`, `fix/fix-name`) instead of committing directly to `main`.
- **Option 1 Workflow**: Default to the complete PR cycle: create a dedicated branch, commit and push changes, open a PR, wait for GHA pipeline success, merge the PR, and pull updates.
- **Branch Cleanup & Sync**: After merging a PR, prune and clean up all merged local and remote branches, keeping the workspace synchronized and organized.

## 2. Workspace Focus
- **Backend First**: The development effort is focused strictly on the Java/Spring Boot backend located in the `/backend` directory.

## 3. Development Workflow
- **Baby-steps**: Always break down large tasks into smaller, incremental phases. Update the implementation plan and task lists (`task.md`) continuously.
- **IntelliJ Run Configurations**: You MUST always check, maintain, and create XML run configuration files inside `.idea/runConfigurations/` whenever adding new entry points or test structures, ensuring the user has access to quick execution buttons directly inside the IntelliJ IDE.
- **User Validation Request**: Regardless of whether you have executed and verified tests/builds successfully in the terminal, you MUST always explicitly prompt the user to validate the results on their end to guarantee absolute correctness.

## 4. Twilio SMS Configuration (Privacy & Staging)
- **Mocked Twilio**: For safety, cost, and privacy reasons (since this is a public repository), the Twilio SMS service must remain simulated/mocked in all local development, CI, and homologation (staging) environments. No real SMS should ever be sent. The source code must look fully implemented, but dynamically bypass real API calls using placeholder/dummy configuration variables.

## 5. IDE Organization & Container Guidelines
- **Independent IDE Projects**: Do NOT open the repository root folder as a single IDE project. The `/backend` folder must be opened as an independent project in IntelliJ IDEA, and the `/frontend` folder as an independent project in WebStorm.
- **Independent .idea Folders**: Since the projects are opened separately, all shared run configurations (`.idea/runConfigurations/`) and project name files (`.idea/.name`) MUST be stored inside their respective subfolders (`/backend/.idea/` and `/frontend/.idea/`), never at the repository root.
- **Shared Run Configurations**: Track all shared configurations in `.idea/runConfigurations/` as XML files. They must use relative paths (e.g. `$PROJECT_DIR$`) to ensure portability across developer machines.
- **Project Renaming**: Prevent name collisions in multi-project workspaces by adding a `.idea/.name` file containing the specific project name (e.g. `DSMeta - Frontend` in frontend and `DSMeta - Backend` in backend).
- **Gitignore Hygiene**: Ensure `.gitignore` ignores personal IDE files (`workspace.xml`, `compiler.xml`, etc.) while explicitly tracking shared run configurations (`!.idea/runConfigurations/**`) and project names (`!.idea/.name`).
- **No Anonymous Volumes**: Docker Compose files (e.g. `docker-compose-dev.yml`) must map container data paths to named volumes (e.g. `postgres_data_dev`) to avoid leaking random anonymous volumes on the developer's machine.
- **IDE Volume Cleanup**: Configure Docker Compose run configurations in the IDE XML to delete volumes on down (`<option name="removeVolumesOnDown" value="true" />`).
- **Sequential Validation**: When prompting the user for validation, always follow a strict sequence:
  1. **Phase 1: Backend Validation**: Direct the user to open `/backend` in IntelliJ IDEA, run the database services via `docker-compose (Dev)`, run the backend app via `Run Application (H2)`, and execute backend unit tests via `Run Tests (JUnit)`.
  2. **Phase 2: Frontend Validation**: Only after backend validation is successful, direct the user to open `/frontend` in WebStorm, start the UI server via `Run Frontend`, and execute frontend tests via `Run Tests (Vitest)`.
- **Explicit Prompts**: Always write highly detailed validation prompts. The AI agent MUST physically read the `.idea/runConfigurations/` directory in the active project root before mentioning or proposing any execution configs, verifying the exact `name` attribute in the XML, and referencing it literally. Specify which IDE to use and which directory path is being tested.
- **TypeScript & Vitest Rules**:
  - Ensure `tsconfig.node.json` uses `"moduleResolution": "node"` (not `Classic`).
  - Import `defineConfig` directly from `'vitest/config'` instead of adding global triple-slash comments (`/// <reference types="vitest" />`).
  - In tests that validate negative paths (like API errors), mock `console.error` (e.g. `vi.spyOn(console, "error").mockImplementation(() => {})`) and restore it afterward to keep the console and test run outputs clean.
