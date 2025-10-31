#!/bin/bash
#
# Simulate GitHub Actions CI build locally
# This runs the same build steps as CI to verify changes before pushing
#
# Usage: ./build-scripts/ci-build-local.sh [java-version]
#
# Examples:
#   ./build-scripts/ci-build-local.sh        # Use current Java version
#   ./build-scripts/ci-build-local.sh 17     # Verify with Java 17
#   ./build-scripts/ci-build-local.sh 21     # Verify with Java 21
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

# Check Java version if specified
if [ -n "$1" ]; then
    REQUESTED_JAVA="$1"
    CURRENT_JAVA=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$CURRENT_JAVA" != "$REQUESTED_JAVA" ]; then
        echo "⚠️  Warning: Requested Java $REQUESTED_JAVA but current version is $CURRENT_JAVA"
        echo "Set JAVA_HOME to Java $REQUESTED_JAVA or remove version argument"
        exit 1
    fi
fi

echo "======================================"
echo "CI Build Simulation (GitHub Actions)"
echo "======================================"
echo ""

# Step 1: Build all modules without tests (like CI Job 1)
echo "Step 1: Building all modules (no tests)..."
echo "Command: mvn --batch-mode --errors --fail-at-end --show-version -T 1C clean install -DskipTests"
echo ""

mvn --batch-mode --errors --fail-at-end --show-version -T 1C clean install -DskipTests

echo ""
echo "======================================"
echo "✅ Build completed successfully!"
echo "======================================"
echo ""
echo "Next steps:"
echo "1. Run specific test groups:"
echo "   ./build-scripts/ci-test-group.sh libs-platform-core"
echo "   ./build-scripts/ci-test-group.sh catalog-core"
echo ""
echo "2. Or run pre-commit/pre-push hooks:"
echo "   pre-commit run --all-files"
echo "   pre-commit run --hook-stage pre-push --all-files"
echo ""
echo "3. Push to GitHub to run full CI with parallel testing"
