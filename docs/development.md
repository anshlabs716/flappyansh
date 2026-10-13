# Development

This page describes how to set up a development environment and work on
Flappy Ansh.

## Requirements

- JDK 21 or newer

## Getting the code

```bash
git clone https://github.com/anshlabs716/flappyansh.git
cd flappyansh
```

## Build

```bash
javac -d out FlappyAnshPro.java
```

## Run

```bash
java -cp out FlappyAnshPro
```

## Test

```bash
# Manual play testing (see docs/testing.md)
```

## Workflow

- Branch from `main` (see [CONTRIBUTING.md](../CONTRIBUTING.md)).
- Keep changes focused and documented.
- Update `CHANGELOG.md` for user-facing changes.
