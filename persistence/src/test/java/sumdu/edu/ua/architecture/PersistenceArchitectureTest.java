package sumdu.edu.ua.persistence;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class PersistenceArchitectureTest {
    @Test
    void persistenceShouldNotDependOnWeb() {
        JavaClasses imported = new ClassFileImporter()
                .importPackages("sumdu.edu.ua");

        noClasses()
                .that().resideInAPackage("..persistence..")
                .should().dependOnClassesThat()
                .resideInAPackage("..web..")
                .check(imported);
    }
}