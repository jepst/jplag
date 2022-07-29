package de.jplag.emf;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenList;
import de.jplag.TokenPrinter;
import de.jplag.testutils.TestErrorConsumer;

class MinimalEmfFrontendTest {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "models");
    private static final String[] TEST_SUBJECT = {"bookstore.ecore", "bookstoreExtended.ecore", "bookstoreRenamed.ecore"};

    private de.jplag.Language frontend;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        TestErrorConsumer consumer = new TestErrorConsumer();
        frontend = new Language(consumer);
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testBookstoreMetamodels() {
        TokenList result = frontend.parse(baseDirectory, TEST_SUBJECT);
        List<String> treeViewFiles = Arrays.stream(TEST_SUBJECT).map(it -> it + Language.VIEW_FILE_SUFFIX).collect(toList());

        logger.debug(TokenPrinter.printTokens(result, baseDirectory, treeViewFiles));
        Field[] fields = MetamodelTokenConstants.class.getFields();
        var constants = Arrays.stream(fields).map(Field::getName).filter(it -> !it.equals("NUM_DIFF_TOKENS")).collect(toList());
        logger.info(("Handcrafted token set: " + constants));
        logger.info("Parsed tokens: " + result.allTokens().toString());
        assertEquals(21, constants.size());
        assertEquals(43, result.size());
    }

    @AfterEach
    public void tearDown() {
        File baseFile = new File(BASE_PATH.toString());
        Arrays.stream(baseFile.listFiles()).filter(it -> it.getName().endsWith(Language.VIEW_FILE_SUFFIX)).forEach(File::delete);
    }

}
