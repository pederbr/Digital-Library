package fxui;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DigLibE2ETest extends ApplicationTest {
    
    @Override
    @Start
    public void start(Stage stage) throws IOException, TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.hideStage();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DigLib.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() throws TimeoutException {
        FxToolkit.setupStage(Stage::show);
        WaitForAsyncUtils.waitForFxEvents();
        sleep(2000); 
    }

    @Test
    @Order(1)
    public void testUploadBook() {
        clickOn("#uploadTab");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#yearField").write("2808");
        clickOn("#ISBNField").write("12394");
        clickOn("#uploadBook");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);

        Text titleError = lookup("#titleError").query();
        Text yearError = lookup("#yearError").query();
        Text isbnError = lookup("#ISBNError").query();
        
        assertTrue(titleError.isVisible(), "Title error should be visible");
        assertTrue(yearError.isVisible(), "Year error should be visible");
        assertTrue(isbnError.isVisible(), "ISBN error should be visible");
        
        clickOn("#yearField").press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A)
            .release(KeyCode.CONTROL).press(KeyCode.DELETE).release(KeyCode.DELETE);
        clickOn("#ISBNField").press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A)
            .release(KeyCode.CONTROL).press(KeyCode.DELETE).release(KeyCode.DELETE);

        clickOn("#titleField").write("Test Title");
        clickOn("#authorField").write("Test Author");
        clickOn("#genreListField").write("Action");
        clickOn("#yearField").write("2021");
        clickOn("#ISBNField").write("1234567890123");

        clickOn("#uploadBook");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(2000);

        assertTrue(lookup("#titleField").queryTextInputControl().getText().isEmpty(), 
            "Title field should be cleared");
        assertTrue(lookup("#authorField").queryTextInputControl().getText().isEmpty(), 
            "Author field should be cleared");
        assertTrue(lookup("#yearField").queryTextInputControl().getText().isEmpty(), 
            "Year field should be cleared");
        assertTrue(lookup("#ISBNField").queryTextInputControl().getText().isEmpty(), 
            "ISBN field should be cleared");
    }

    @Test
    @Order(2)
    public void testChangingTabs() {
        clickOn("#viewTab");
        
        TextArea displayField = lookup("#displayField").query();
        assertFalse(displayField.getText().equals("No books selected"), "should display previous book content");

        clickOn("#listTab");
        clickOn("#TestTitleButton"); 
        
        clickOn("#viewTab");
        TextArea metadataField = lookup("#metadataField").query();
        assertTrue(metadataField.getText().contains("Title: Test Title"), "Metadata should show book title");
    }

    @Test
    @Order(3)
    public void testDeleteButton() {
        clickOn("#listTab");
        clickOn("#TestTitleButton"); 
        clickOn("#deleteBook");
        
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
        
        clickOn("#viewTab");
        TextArea displayField = lookup("#displayField").query();
        assertTrue(displayField.getText().equals("no book selected"), "View should show no books selected");
    }

    @AfterEach
    public void tearDown() {
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
} 