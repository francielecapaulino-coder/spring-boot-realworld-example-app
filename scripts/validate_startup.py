#!/usr/bin/env python3
"""
validate_startup.py — valida startup e shutdown da stack via Docker Compose.

Comportamento (US-03.06):
  1. docker compose up -d
  2. Aguarda até STARTUP_TIMEOUT_S por /actuator/health -> {"status": "UP"}
  3. docker compose stop
  4. Aguarda até SHUTDOWN_TIMEOUT_S confirmando que os containers pararam

Exit code 0: sucesso | Exit code 1: falha com mensagem descritiva (stderr).

Dependência: requests  (pip install -r requirements-scripts.txt)
"""

import subprocess
import sys
import time

try:
    import requests
except ImportError:
    print(
        "✗ Missing dependency 'requests'. Install with: "
        "pip install -r requirements-scripts.txt",
        file=sys.stderr,
    )
    sys.exit(1)

HEALTH_URL = "http://localhost:8080/actuator/health"
STARTUP_TIMEOUT_S = 60
SHUTDOWN_TIMEOUT_S = 30
POLL_INTERVAL_S = 3


def run(cmd):
    """Run a command capturing stdout/stderr.

    Never raises: a missing binary (e.g. docker not installed) is reported as a
    failed CompletedProcess with returncode 127, mirroring shell semantics.
    """
    try:
        return subprocess.run(cmd, capture_output=True, text=True)
    except FileNotFoundError:
        return subprocess.CompletedProcess(
            cmd,
            returncode=127,
            stdout="",
            stderr=f"command not found: {cmd[0]!r} (is Docker installed?)",
        )


def wait_for_health(timeout):
    """Poll /actuator/health until it returns {"status": "UP"} or timeout."""
    deadline = time.time() + timeout
    while time.time() < deadline:
        try:
            response = requests.get(HEALTH_URL, timeout=5)
            if response.status_code == 200 and response.json().get("status") == "UP":
                return True
        except Exception:
            pass
        time.sleep(POLL_INTERVAL_S)
    return False


def wait_for_shutdown(timeout):
    """Poll `docker compose ps -q` until no containers are running or timeout."""
    deadline = time.time() + timeout
    while time.time() < deadline:
        result = run(["docker", "compose", "ps", "-q"])
        if result.returncode == 0 and not result.stdout.strip():
            return True
        time.sleep(POLL_INTERVAL_S)
    return False


def main():
    print("→ Starting docker compose...")
    result = run(["docker", "compose", "up", "-d"])
    if result.returncode != 0:
        print(f"✗ docker compose up failed:\n{result.stderr}", file=sys.stderr)
        return 1

    print(f"→ Waiting up to {STARTUP_TIMEOUT_S}s for /actuator/health UP...")
    start = time.time()
    if not wait_for_health(STARTUP_TIMEOUT_S):
        print(
            f"✗ Startup validation failed: timeout after {STARTUP_TIMEOUT_S}s",
            file=sys.stderr,
        )
        run(["docker", "compose", "down"])
        return 1

    elapsed = round(time.time() - start)
    print(f"✓ Startup validated in {elapsed}s")

    print("→ Stopping docker compose...")
    stop_result = run(["docker", "compose", "stop"])
    if stop_result.returncode != 0:
        print(f"✗ docker compose stop failed:\n{stop_result.stderr}", file=sys.stderr)
        return 1

    if not wait_for_shutdown(SHUTDOWN_TIMEOUT_S):
        print(
            "✗ Shutdown validation failed: containers still running after "
            f"{SHUTDOWN_TIMEOUT_S}s",
            file=sys.stderr,
        )
        return 1

    print("✓ Shutdown validated")
    return 0


if __name__ == "__main__":
    sys.exit(main())
