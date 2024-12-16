package fxui;

import java.net.http.HttpClient;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import fxui.controllers.MainController;
import fxui.util.MockHttpClient;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DigLibIntegrationTest extends ApplicationTest {
    private MockHttpClient mockClient;
    private DigLibApp app;
    private MainController controller;

    private void buildHttpClient() {
        System.out.println("building http client");
        mockClient = new MockHttpClient();
        mockClient.addResponse("https://api.diglib.no/books", 
            "{\"content\":[{\"id\":1,\"title\":\"Test Book\",\"author\":\"Test Author \",\"year\":2023,\"genre\":\"Fiction\",\"isbn\":\"1234567890123\",\"fileName\":\"test.txt\"}],\"pageable\":{\"pageNumber\":0,\"pageSize\":10,\"sort\":{\"empty\":false,\"sorted\":true,\"unsorted\":false},\"offset\":0,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalElements\":10,\"totalPages\":1,\"first\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":false,\"sorted\":true,\"unsorted\":false},\"numberOfElements\":10,\"empty\":false}", 200);
        mockClient.addResponse("https://api.diglib.no/genres", 
            "[\"Fiction\",\"Non-fiction\"]", 200);
        mockClient.addResponse("https://api.diglib.no/books/1/content?page=0&pageSize=1000",
        "{\"content\":\"Test content\",\"pageNumber\":0,\"totalPages\":1,\"pageSize\":12,\"totalSize\":12,\"readingTime\":\"0 hours, 0 minutes\"}", 200);
        mockClient.addResponse(null, null, 0);
    }
    @Override
    public void start(Stage stage) throws Exception {
        buildHttpClient();
        app = new TestableDigLibApp(mockClient);
        app.start(stage);
        controller = ((TestableDigLibApp) app).getMainController();
    }

    @Test
    @Order(1)
    void testInitialBookListLoading() {
        System.out.println("testing initial book list loading");
        assertNotNull(controller.getBookList());
        assertEquals(1, controller.getBookList().size());
        assertEquals("Test Book", controller.getBookList().get(0).getTitle());
    }        

    @Test
    @Order(2)
    public void testUploadBook() {
        System.out.println("testing upload book");
        clickOn("#uploadTab");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#yearField").write("2999");
        clickOn("#ISBNField").write("682684");
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
    @Order(3)
    public void testChangingTabs() {
        System.out.println("testing changing tabs");
        clickOn("#viewTab");
        
        TextArea displayField = lookup("#displayField").query();
        assertFalse(displayField.getText().equals("No books selected"), "should display previous book content");

        clickOn("#listTab");
        clickOn("#TestBookButton"); 
        
        
        clickOn("#viewTab");
        TextArea metadataField = lookup("#metadataField").query();
        assertTrue(metadataField.getText().contains("Title: Test Book"), "Metadata should show book title");
    }

    @Test
    @Order(4)
    public void testDeleteButton() {
        System.out.println("testing delete button");
        clickOn("#listTab");
        clickOn("#TestBookButton"); 
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

    private static class TestableDigLibApp extends DigLibApp {
        private final MainController mainController;

        public TestableDigLibApp(HttpClient httpClient) {
            super(httpClient);
            this.mainController = new MainController(httpClient);
            System.out.println("creating main controller");
        }

        public MainController getMainController() {
            return mainController;
        }

        @Override
        protected Object createController(Class<?> type) {
            if (type == MainController.class) {
                return mainController;
            }
            return super.createController(type);
        }
    }
}