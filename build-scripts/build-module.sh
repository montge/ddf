#!/bin/bash
#
# Fast modular build script for DDF development
# Usage: ./build-scripts/build-module.sh <module-path> [maven-args]
#
# Examples:
#   ./build-scripts/build-module.sh platform/security
#   ./build-scripts/build-module.sh catalog/core/catalog-core-api -DskipTests=false
#   ./build-scripts/build-module.sh libs clean install
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

if [ $# -lt 1 ]; then
    echo "Usage: $0 <module-path> [maven-args]"
    echo ""
    echo "Available top-level modules:"
    echo "  - platform"
    echo "  - catalog"
    echo "  - libs"
    echo "  - features"
    echo "  - distribution"
    echo ""
    echo "Examples:"
    echo "  $0 platform/security"
    echo "  $0 catalog/core install -DskipTests=false"
    exit 1
fi

MODULE_PATH="$1"
shift

# Parse arguments - check if any lifecycle phase or goal is specified
MAVEN_ARGS="$@"
HAS_GOAL=false
for arg in $MAVEN_ARGS; do
    if [[ ! "$arg" =~ ^- ]]; then
        HAS_GOAL=true
        break
    fi
done

# Default to install if no goal specified
if [ "$HAS_GOAL" = false ]; then
    MAVEN_ARGS="install ${MAVEN_ARGS}"
fi

# Default to fast build unless tests are explicitly requested
if [[ ! "$MAVEN_ARGS" =~ "skipTests=false" ]]; then
    MAVEN_ARGS="${MAVEN_ARGS} -DskipTests"
fi

# Change to module directory
cd "$PROJECT_ROOT/$MODULE_PATH"

echo "======================================"
echo "Building module: $MODULE_PATH"
echo "Maven command: mvn ${MAVEN_ARGS}"
echo "======================================"

# Run maven build
mvn ${MAVEN_ARGS}

echo ""
echo "✓ Module build completed: $MODULE_PATH"
