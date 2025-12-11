/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.services.schematron;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.validation.ValidationException;
import ddf.catalog.validation.report.MetacardValidationReport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

@EnableRuleMigrationSupport
public class SchematronValidationServiceTest {

  private static File fileWithSpaces;

  @Rule public TemporaryFolder testFolder = new TemporaryFolder();

  @BeforeEach
  public void setup() throws IOException {
    URL src = SchematronValidationServiceTest.class.getClassLoader().getResource("dog_legs.sch");
    fileWithSpaces =
        Paths.get(testFolder.getRoot().getAbsolutePath())
            .resolve("folder with spaces")
            .resolve("dog_legs.sch")
            .toFile();
    FileUtils.copyURLToFile(src, fileWithSpaces);
  }

  @Test
  public void testSingleSchematron()
      throws ValidationException, IOException, SchematronInitializationException {
    getService("dog_legs.sch").validate(getMetacard("dog_4leg_3paw.xml"));
  }

  @Test
  public void testNullorEmptyMetadata()
      throws ValidationException, IOException, SchematronInitializationException {
    MetacardImpl card = new MetacardImpl();
    SchematronValidationService service = getService("dog_legs.sch");
    service.validate(card);
    card.setMetadata("");
    service.validate(card);
    assertThat("No exceptions were thrown validating null/empty metadata", true, is(true));
  }

  @Test
  public void testMultipleSchematron() throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_legs.sch", "dog_paws.sch");
    MetacardImpl metacard = getMetacard("dog_4leg_3paw.xml");
    assertThrows(ValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testWithWarnings() throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_legs.sch", "dog_paws.sch");
    MetacardImpl metacard = getMetacard("dog_4leg_3paw.xml");
    assertThrows(ValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testWithErrors() throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_legs.sch", "dog_paws.sch");
    MetacardImpl metacard = getMetacard("dog_3leg_3paw.xml");
    assertThrows(ValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testWithWarningsAndSupressWarnings()
      throws ValidationException, IOException, SchematronInitializationException {
    SchematronValidationService service =
        getService(true, null, true, "dog_legs.sch", "dog_paws.sch");
    service.validate(getMetacard("dog_4leg_3paw.xml"));
  }

  @Test
  public void testWithErrorsAndSuppressWarnings()
      throws IOException, SchematronInitializationException {
    SchematronValidationService service =
        getService(true, null, true, "dog_legs.sch", "dog_paws.sch");
    MetacardImpl metacard = getMetacard("dog_3leg_3paw.xml");
    assertThrows(ValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testWithCorrectNamespace()
      throws ValidationException, IOException, SchematronInitializationException {
    SchematronValidationService service =
        getService(false, "doggy-namespace", true, "dog_legs.sch", "dog_paws.sch");
    service.validate(getMetacard("dog_4leg_4paw_namespace.xml"));
  }

  @Test
  public void testWithIncorrectNamespace()
      throws ValidationException, IOException, SchematronInitializationException {
    SchematronValidationService service =
        getService(false, "this-is-the-wrong-namespace", true, "dog_legs.sch", "dog_paws.sch");
    Optional<MetacardValidationReport> report =
        service.validateMetacard(getMetacard("dog_4leg_4paw_namespace.xml"));
    assertThat(report.isPresent(), is(true));
    assertThat(report.get().getMetacardValidationViolations(), is(empty()));
  }

  @Test
  public void testSchematronFileNotFound() throws IOException, SchematronInitializationException {
    SchematronValidationService service =
        getService(false, null, false, "definitely_does_not_exist.sch");
    MetacardImpl metacard = getMetacard("dog_4leg_4paw.xml");
    assertThrows(ValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testDocumentFunctionWithIncorrectRelativePathAndValidName()
      throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_name.sch");
    MetacardImpl metacard = getMetacard("dog_valid_name.xml");
    assertThrows(SchematronValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testDocumentFunctionWithIncorrectRelativePathAndInvalidName()
      throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_name.sch");
    MetacardImpl metacard = getMetacard("dog_invalid_name.xml");
    assertThrows(SchematronValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testDocumentFunctionWithCorrectRelativePathAndValidName()
      throws ValidationException, IOException, SchematronInitializationException {
    getService("dog_name_relative.sch").validate(getMetacard("dog_valid_name.xml"));
  }

  @Test
  public void testDocumentFunctionWithCorrectRelativePathAndInvalidName()
      throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_name_relative.sch");
    MetacardImpl metacard = getMetacard("dog_invalid_name.xml");
    assertThrows(SchematronValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testWithSpacesInPathToSchematronFile()
      throws IOException, SchematronInitializationException {
    SchematronValidationService service = getService(false, null, false, fileWithSpaces.toString());
    MetacardImpl metacard = getMetacard("dog_3leg_3paw.xml");
    assertThrows(SchematronValidationException.class, () -> service.validate(metacard));
  }

  @Test
  public void testWarningsAndErrorsAreSanitized()
      throws ValidationException, IOException, SchematronInitializationException {
    SchematronValidationService service = getService("dog_legs.sch", "dog_paws.sch");
    // test warnings
    try {
      service.validate(getMetacard("dog_4leg_3paw.xml"));
    } catch (SchematronValidationException ex) {
      for (String warning : ex.getWarnings()) {
        assertThat(warning.equals(SchematronValidationService.sanitize(warning)), is(true));
      }
    }
    // test errors
    try {
      service.validate(getMetacard("dog_3leg_3paw.xml"));
    } catch (SchematronValidationException ex) {
      for (String error : ex.getErrors()) {
        assertThat(error.equals(SchematronValidationService.sanitize(error)), is(true));
      }
    }
    // sanitize is called twice for each validate call
    verify(service, times(4)).sanitize(anyString());
  }

  @Test
  public void testSanitizationChangesNothing() {
    String str = "ontattoinewerunfromsandpeople";
    // verify that nothing changes
    assertThat(str, is(SchematronValidationService.sanitize(str)));
    str = "princess leia";
    // verify that nothing changes again
    assertThat(str, is(SchematronValidationService.sanitize(str)));
  }

  @Test
  public void testSanitizationChangesInput() {
    String toTrimString = " luke skywalker     ";
    String trimmedString = "luke skywalker";
    // verify the trim
    assertThat(trimmedString, is(SchematronValidationService.sanitize(toTrimString)));
    String extraWhitespace = "dagobah  system";
    String singleWhitespace = "dagobah system";
    // verify that a double whitespace gets truncated to single whitespace
    assertThat(singleWhitespace, is(SchematronValidationService.sanitize(extraWhitespace)));
    String delimiters =
        " Return \t\t    of the Jedi was originally called\n  Revenge of the Jedi\nBut Jedi\n\r don't\r\n take revenge\n   ";
    String noDelimiters =
        "Return of the Jedi was originally called Revenge of the Jedi But Jedi don't take revenge";
    // verify that newlines, tabs, and extra whitespace are removed
    assertThat(noDelimiters, is(SchematronValidationService.sanitize(delimiters)));
  }

  private MetacardImpl getMetacard(String filename) throws IOException {
    String metadata = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filename));
    MetacardImpl metacard = new MetacardImpl();
    metacard.setMetadata(metadata);
    return metacard;
  }

  private SchematronValidationService getService(String... schematronFiles)
      throws SchematronInitializationException {
    return getService(false, null, true, schematronFiles);
  }

  private SchematronValidationService getService(
      boolean suppressWarnings, String namespace, boolean useClassLoader, String... schematronFiles)
      throws SchematronInitializationException {

    SchematronValidationService service = Mockito.spy(new SchematronValidationService());
    service.setSuppressWarnings(suppressWarnings);
    service.setNamespace(namespace);

    ArrayList<String> schemaFiles = new ArrayList<>();
    for (String schematronFile : schematronFiles) {
      String resourcePath = schematronFile;
      if (useClassLoader) {
        URL schematronResource =
            SchematronValidationServiceTest.class.getClassLoader().getResource(schematronFile);
        if (schematronResource == null) {
          fail("The Schematron Resource came back null. Was the resources folder removed?");
        }
        resourcePath = new File(schematronResource.getFile()).getAbsolutePath();
      }
      schemaFiles.add(resourcePath);
    }
    service.setSchematronFileNames(schemaFiles);

    service.init();
    return service;
  }
}
