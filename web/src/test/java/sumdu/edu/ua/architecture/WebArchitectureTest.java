package sumdu.edu.ua.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class WebArchitectureTest {
    @Test
    void webShouldNotDependOnPersistence() {
        JavaClasses imported = new ClassFileImporter()
                .importPackages("sumdu.edu.ua.web");

        noClasses()
                .that().resideInAPackage("..web..")
                .and().doNotHaveSimpleName("Beans") // Дозволяємо фабриці залежати від БД
                .should().dependOnClassesThat()
                .resideInAPackage("..persistence..")
                .check(imported);
    }
}