# CXF 4.x Karaf Support Proposal for DDF

## Problem Statement

Apache CXF 4.x removed OSGi/Karaf support (CXF-8371), blocking DDF's Jakarta EE migration:
- No Karaf features.xml
- No Blueprint extension
- OSGi headers inconsistent

DDF requires CXF for JAX-RS/JAX-WS endpoints and security (SAML SSO, OAuth2, WS-Security).

## Current State

| Component | Status |
|-----------|--------|
| CXF-9086 JIRA | Open - "Bring back OSGi support" |
| cxf-karaf repo | Proposed by JB Onofré (Dec 2024), not yet created |
| camel-karaf | Uses CXF 4.1.1 via shaded uber-bundle |
| DDF | Stuck on CXF 3.6.x |

## Options Analysis

### Option A: Use camel-karaf CXF Bundles

**Approach:** Leverage `camel-cxf-all` bundle from camel-karaf.

**Pros:**
- Already exists and tested
- Maintained by Apache Camel community
- CXF 4.1.1 support

**Cons:**
- Missing DDF-specific modules:
  - `cxf-rt-rs-security-sso-saml` (SAML SSO)
  - `cxf-rt-rs-security-oauth2` (OAuth2)
  - `cxf-rt-ws-security` (WS-Security)
- Tied to Camel release cycle
- May include unnecessary Camel dependencies

**Verdict:** Partial solution - would need supplemental wrappers.

### Option B: Create ddf-cxf-karaf Module

**Approach:** Create DDF-specific CXF uber-bundle with all required modules.

**Structure:**
```
libs/cxf-karaf/
├── pom.xml
├── cxf-core-all/           # Core CXF + transports
│   └── pom.xml             # Shaded bundle
├── cxf-security-all/       # Security modules
│   └── pom.xml             # SAML, OAuth2, WS-Security
└── features/
    └── feature.xml         # Karaf features
```

**Included Modules:**
```xml
<!-- Core (from camel-karaf pattern) -->
cxf-core
cxf-rt-bindings-soap
cxf-rt-bindings-xml
cxf-rt-databinding-jaxb
cxf-rt-frontend-jaxrs
cxf-rt-frontend-jaxws
cxf-rt-transports-http
cxf-rt-wsdl
cxf-rt-ws-policy
cxf-rt-ws-addr

<!-- Security (DDF-specific) -->
cxf-rt-security
cxf-rt-rs-security-xml
cxf-rt-rs-security-sso-saml
cxf-rt-rs-security-oauth2
cxf-rt-ws-security
```

**Pros:**
- Full control over included modules
- Tailored to DDF security requirements
- Can track latest CXF releases independently

**Cons:**
- Maintenance burden
- Duplicates work if cxf-karaf is eventually created

**Verdict:** Best near-term solution for DDF.

### Option C: Contribute to Upstream cxf-karaf

**Approach:** Help create official Apache cxf-karaf repository.

**Required Contributions:**
1. Phase 1: Restore OSGi manifest headers (in progress per CXF-9086)
2. Phase 2: Create bundle wrappers (similar to camel-karaf)
3. Phase 3: Create Karaf features.xml

**Timeline:** CXF community estimates Q2 2025+ for ecosystem readiness.

**Pros:**
- Benefits entire community
- Reduces long-term maintenance
- Official Apache support

**Cons:**
- Depends on community bandwidth
- Longer timeline
- Blocked by Pax Web/Aries ecosystem readiness

**Verdict:** Long-term goal, but not blocking for DDF.

## Recommendation

**Hybrid Approach: Option B now, contribute to Option C**

1. **Immediate (Q1 2025):** Create `libs/ddf-cxf-karaf` following camel-karaf pattern
2. **Parallel:** Engage with CXF-9086 to contribute manifest header work
3. **Future:** Migrate to official cxf-karaf when available

## Implementation Plan

### Phase 1: ddf-cxf-karaf Creation

```
Week 1-2:
- [ ] Create libs/ddf-cxf-karaf module structure
- [ ] Configure maven-shade-plugin for cxf-core-all
- [ ] Add OSGi manifest generation (bundle-plugin)
- [ ] Test basic JAX-RS endpoint

Week 3-4:
- [ ] Add cxf-security-all bundle (SAML, OAuth2)
- [ ] Create Karaf features.xml
- [ ] Update DDF security features to use new bundles
- [ ] Integration testing
```

### Phase 2: Jakarta Migration (after cxf-karaf works)

```
- [ ] Update all CXF imports to 4.x
- [ ] Run OpenRewrite jakarta migration
- [ ] Update Blueprint configurations
- [ ] Full integration testing
```

## Technical Details

### Maven Shade Configuration (from camel-karaf)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
            <configuration>
                <artifactSet>
                    <includes>
                        <include>org.apache.cxf:cxf-core</include>
                        <include>org.apache.cxf:cxf-rt-*</include>
                    </includes>
                </artifactSet>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### OSGi Manifest Requirements

```properties
Bundle-Activator: org.codice.ddf.cxf.osgi.CxfActivator
Export-Package: org.apache.cxf.*;version=${cxf.version}
Import-Package:
    jakarta.servlet*;version="[6.0,7)",
    jakarta.ws.rs*;version="[3.1,4)",
    jakarta.xml.bind*;version="[4.0,5)",
    ...
DynamicImport-Package: org.apache.cxf.bus,org.apache.cxf.*
```

## Dependencies

- Pax Web 9.x+ (Jetty 11/12, Jakarta Servlet 5/6)
- Apache Aries Blueprint (Jakarta-compatible version)
- ServiceMix Spring bundles 6.2.8+

## Risks

| Risk | Mitigation |
|------|------------|
| Pax Web not Jakarta-ready | Track Pax Web 10.x development |
| Blueprint incompatibilities | Use DS/SCR as fallback |
| Version conflicts | Careful BOM management |

## References

- [CXF-9086: Bring back OSGi support](https://issues.apache.org/jira/browse/CXF-9086)
- [camel-karaf CXF wrapping](https://github.com/apache/camel-karaf/tree/main/components/camel-cxf)
- [cxf-karaf proposal thread](https://www.mail-archive.com/dev@cxf.apache.org/msg23208.html)
