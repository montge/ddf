# Python Virtual Environment Configuration

## Location
Primary Python virtual environment: `~/.venv`

## Usage
- Pre-commit: `~/.venv/bin/pre-commit`
- Pip: `~/.venv/bin/pip`
- Python: `~/.venv/bin/python`

## Activation
```bash
source ~/.venv/bin/activate
```

## Installed Tools
- pre-commit (for git hooks)
- Other Python tools as needed

## Note for Claude Code
When running Python tools for this project, prefer using ~/.venv/bin/ executables
to ensure consistent environment and avoid system Python conflicts.
