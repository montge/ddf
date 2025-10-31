#!/bin/bash
#
# Run specific test groups locally (simulating GitHub Actions parallel test jobs)
#
# Usage: ./build-scripts/ci-test-group.sh <group-name>
#
# Available groups:
#   libs-platform-core             - Libs and platform core modules
#   catalog-core                   - Catalog core functionality
#   features-transformers-plugins  - Features, transformers, and plugins
#   admin-solr-spatial             - Admin, Solr, and spatial modules
#   integration                    - Integration tests (Pax Exam)
#
# Examples:
#   ./build-scripts/ci-test-group.sh libs-platform-core
#   ./build-scripts/ci-test-group.sh catalog-core
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

if [ $# -lt 1 ]; then
    echo "Usage: $0 <group-name>"
    echo ""
    echo "Available test groups:"
    echo "  libs-platform-core             - Libs and platform core"
    echo "  catalog-core                   - Catalog core"
    echo "  features-transformers-plugins  - Features, transformers, plugins"
    echo "  admin-solr-spatial             - Admin, Solr, spatial"
    echo "  integration                    - Integration tests"
    exit 1
fi

GROUP="$1"
cd "$PROJECT_ROOT"

echo "======================================"
echo "Running Test Group: $GROUP"
echo "======================================"
echo ""

case "$GROUP" in
    libs-platform-core)
        echo "Testing: libs, platform/osgi, platform/util, platform/security, etc."
        mvn --batch-mode --errors --fail-at-end test \
            -pl libs/,platform/osgi/,platform/util/,platform/security/,platform/platform-api/,platform/platform-configuration/,platform/branding-api/,platform/parser/,platform/mime/,platform/io/ \
            -am
        ;;

    catalog-core)
        echo "Testing: catalog/core, catalog/security, catalog/common, etc."
        mvn --batch-mode --errors --fail-at-end test \
            -pl catalog/core/,catalog/security/,catalog/common/,catalog/measure/,catalog/opensearch/ \
            -am
        ;;

    features-transformers-plugins)
        echo "Testing: catalog transformers, plugins, validators, features"
        mvn --batch-mode --errors --fail-at-end test \
            -pl catalog/transformer/,catalog/plugin/,catalog/validator/,catalog/schematron/,features/ \
            -am
        ;;

    admin-solr-spatial)
        echo "Testing: admin, Solr, spatial, REST, UI, distribution"
        mvn --batch-mode --errors --fail-at-end test \
            -pl platform/admin/,platform/solr/,platform/persistence/,platform/metrics/,catalog/solr/,catalog/spatial/,catalog/rest/,catalog/ui/,distribution/ \
            -am
        ;;

    integration)
        echo "Testing: Integration tests (Pax Exam)"
        echo "Note: This runs 'mvn verify' to execute failsafe integration tests"
        mvn --batch-mode --errors --fail-at-end verify \
            -pl features/utilities/,features/admin/,features/apps/,features/branding/,features/install-profiles/,features/security/,features/solr/ \
            -am
        ;;

    *)
        echo "❌ Unknown test group: $GROUP"
        echo ""
        echo "Available groups:"
        echo "  libs-platform-core, catalog-core, features-transformers-plugins,"
        echo "  admin-solr-spatial, integration"
        exit 1
        ;;
esac

echo ""
echo "======================================"
echo "✅ Test group completed: $GROUP"
echo "======================================"
