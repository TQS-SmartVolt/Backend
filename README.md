
# Backend

## Development

We use [**pre-commit**](https://pre-commit.com) to ensure code quality by running checks before each Git commit.

### Set Up Pre-commit

#### 1. Install Pre-commit
Create and activate a virtual environment, then install `pre-commit`:

```bash
python3 -m venv env
source env/bin/activate
pip install -r requirements.txt # pre-commit is in requirements.txt
```

#### 2. Install Hooks
Configure Git to run hooks defined in .pre-commit-config.yaml:
```bash
pre-commit install
```

Note: Hooks (e.g., check-yaml, trailing-whitespace) run automatically on git commit. If files are modified, re-stage them (git add .) and retry.

#### 3. Run Hooks Manually (Optional)
Check all files:

```bash
pre-commit run --all-files
```

> Tip: For issues, see Pre-commit Docs or ask the team. `git commit --no-verify` can bypass hooks, but use it cautiously.
