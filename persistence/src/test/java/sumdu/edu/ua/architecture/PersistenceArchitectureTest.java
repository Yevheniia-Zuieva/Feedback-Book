package sumdu.edu.ua.architecture;

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
                .that().resideInAPackage("sumdu.edu.ua.persistence..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("sumdu.edu.ua.web..")
                .check(imported);
    }
}