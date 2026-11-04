package sumdu.edu.ua.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CoreArchitectureTest {
    @Test
    void coreShouldNotDependOnWebOrPersistence() {
        JavaClasses imported = new ClassFileImporter()
                .importPackages("sumdu.edu.ua");

        noClasses()
                .that().resideInAPackage("sumdu.edu.ua.core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("sumdu.edu.ua.web..", "sumdu.edu.ua.persistence..")
                .check(imported);
    }
}