# shellcheck shell=bash
# This file is sourced by Solr's startup scripts
# See https://solr.apache.org/guide/taking-solr-to-production.html#environment-overrides-include-scripts

SOLR_OPTS="$SOLR_OPTS -Djute.maxbuffer=2097152"
# SOLR_START_WAIT is read by Solr's bin/solr script
export SOLR_START_WAIT=60
