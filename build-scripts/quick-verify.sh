#!/bin/bash
#
# Quick build verification script
# Builds only core modules to verify compilation without running full build
#
# Usage: ./build-scripts/quick-verify.sh
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo "======================================"
echo "Quick Build Verification"
echo "Building core modules only"
echo "======================================"

# Build only essential modules for quick verification
CORE_MODULES=(
    "libs"
    "platform/security/security-core-api"
    "catalog/core/catalog-core-api"
)

for module in "${CORE_MODULES[@]}"; do
    echo ""
    echo "Building: $module"
    (cd "$module" && mvn clean install -DskipTests -Dcheckstyle.skip=true -Dfmt.skip=true -q)
done

echo ""
echo "✓ Quick verification completed successfully"
echo "All core modules compiled without errors"
