package fxui;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testfx.framework.junit5.ApplicationTest;

import fxui.controllers.ListController;
import fxui.controllers.MainController;
import fxui.controllers.UploadController;
import fxui.controllers.ViewController;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

class DigLibAppTest extends ApplicationTest {
    private HttpClient mockHttpClient;
    private TestableDigLibApp app;  
    private Stage stage;

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) throws IOException, InterruptedException {
        this.stage = stage;
        this.mockHttpClient = mock(HttpClient.class);
        
        // Mock HTTP response for genre request with correct typing
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[\"Fiction\", \"Non-fiction\"]");
        when(mockHttpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
            .thenReturn((HttpResponse) mockResponse);
        
        this.app = new TestableDigLibApp(mockHttpClient);
        app.start(stage);
    }


    @Test
    void testDefaultConstructor() {
        assertNotNull(app, "App should be initialized");
        DigLibApp defaultApp = new DigLibApp();
        assertNotNull(defaultApp, "Default constructor should create valid instance");
    }

    @Test
    void testCustomConstructor() {
        assertNotNull(app, "App should be initialized");
        DigLibApp customApp = new DigLibApp(mockHttpClient);
        assertNotNull(customApp, "Custom constructor should create valid instance");
    }

    @Test
    void testApplicationStart() {
        assertNotNull(stage, "Stage should be initialized");
        assertNotNull(stage.getScene(), "Scene should be created");
        assertEquals("Digital Library", stage.getTitle());
    }

    @Test
    void testControllerCreation() {
        assertNotNull(app.getMainController(), "Main controller should be available");
        assertAll("Controller creation",
            () -> assertNotNull(app.createController(MainController.class)),
            () -> assertNotNull(app.createController(UploadController.class)),
            () -> assertNotNull(app.createController(ViewController.class)),
            () -> assertNotNull(app.createController(ListController.class))
        );
    }

    @Test
    void testInvalidControllerType() {
        DigLibApp testApp = new DigLibApp(mockHttpClient);
        
        assertThrows(IllegalArgumentException.class, 
            () -> testApp.createController(String.class));
    }

    @Test
    void testMainControllerHasHttpClient() {
        assertNotNull(app.getMainController(), "Main controller should be available");
        assertNotNull(app.getMainController().getHttpClient(), "HTTP client should be set");
    }

    @Test
    void testRealFxmlLoading() {
        // Get the TabPane from inside the AnchorPane
        TabPane tabPane = (TabPane) stage.getScene().getRoot().lookup("#tabPane");
        assertNotNull(tabPane, "TabPane should be loaded");
        
        assertEquals(3, tabPane.getTabs().size(), "Should have 3 tabs");
        
        // Test tab existence
        assertAll("Tab existence",
            () -> assertNotNull(tabPane.getTabs().stream()
                .filter(tab -> tab.getId().equals("listTab"))
                .findFirst()
                .orElse(null), "List tab should exist"),
            () -> assertNotNull(tabPane.getTabs().stream()
                .filter(tab -> tab.getId().equals("viewTab"))
                .findFirst()
                .orElse(null), "View tab should exist"),
            () -> assertNotNull(tabPane.getTabs().stream()
                .filter(tab -> tab.getId().equals("uploadTab"))
                .findFirst()
                .orElse(null), "Upload tab should exist")
        );
    }

    @Test
    void testCreateControllerNullType() {
        assertThrows(IllegalArgumentException.class, 
            () -> app.createController(null),
            "Should throw IllegalArgumentException for null type");
    }

    @Test
    void testControllerUniqueness() {
        DigLibApp testApp = new DigLibApp(mockHttpClient);
        Object controller1 = testApp.createController(MainController.class);
        Object controller2 = testApp.createController(MainController.class);
        assertNotNull(controller1);
        assertNotNull(controller2);
        // Verify that we get new instances each time
        assertNotSame(controller1, controller2, "Controllers should be unique instances");
    }

    @Test
    void testControllerDependencyInjection() {
        MainController mainController = (MainController) app.createController(MainController.class);
        UploadController uploadController = (UploadController) app.createController(UploadController.class);
        ViewController viewController = (ViewController) app.createController(ViewController.class);
        ListController listController = (ListController) app.createController(ListController.class);

        assertAll("Controller dependencies",
            () -> assertNotNull(mainController.getHttpClient(), "MainController should have HttpClient"),
            () -> {
                uploadController.setMainController(mainController, mockHttpClient);
                assertNotNull(uploadController.getMainController(), "UploadController should accept MainController");
            },
            () -> {
                viewController.setMainController(mainController, mockHttpClient);
                assertNotNull(viewController.getMainController(), "ViewController should accept MainController");
            },
            () -> {
                listController.setMainController(mainController);
                assertNotNull(listController.getMainController(), "ListController should accept MainController");
            }
        );
    }

    @Test
    void testControllerTypeValidation() {
        assertAll("Controller type validation",
            () -> assertThrows(IllegalArgumentException.class, 
                () -> app.createController(Object.class),
                "Should reject unknown controller type"),
            () -> assertThrows(IllegalArgumentException.class, 
                () -> app.createController(TestableDigLibApp.class),
                "Should reject non-controller class"),
            () -> assertDoesNotThrow(() -> app.createController(MainController.class),
                "Should accept valid controller type")
        );
    }

    @Test
    void testControllerGetters() {
        MainController mainController = app.getMainController();
        UploadController uploadController = (UploadController) app.createController(UploadController.class);
        ViewController viewController = (ViewController) app.createController(ViewController.class);
        ListController listController = (ListController) app.createController(ListController.class);

        assertAll("Controller getters",
            () -> assertEquals(mainController, uploadController.getMainController()),
            () -> assertEquals(mainController, viewController.getMainController()),
            () -> assertEquals(mainController, listController.getMainController())
        );
    }

    @Test
    void testSpecificControllerCreation() {
        DigLibApp testApp = new DigLibApp(mockHttpClient);
        
        assertAll("Individual controller creation",
            () -> assertTrue(testApp.createController(UploadController.class) instanceof UploadController),
            () -> assertTrue(testApp.createController(ViewController.class) instanceof ViewController),
            () -> assertTrue(testApp.createController(ListController.class) instanceof ListController)
        );
    }

    @Test
    void testControllerInitialization() throws IOException, InterruptedException {
        DigLibApp testApp = new DigLibApp(mockHttpClient);
        MainController mainController = (MainController) testApp.createController(MainController.class);
        
        UploadController uploadController = (UploadController) testApp.createController(UploadController.class);
        uploadController.setMainController(mainController, mockHttpClient);
        
        ViewController viewController = (ViewController) testApp.createController(ViewController.class);
        viewController.setMainController(mainController, mockHttpClient);
        
        ListController listController = (ListController) testApp.createController(ListController.class);
        listController.setMainController(mainController);
        
        assertAll("Controller initialization",
            () -> assertNotNull(uploadController.getMainController()),
            () -> assertNotNull(viewController.getMainController()),
            () -> assertNotNull(listController.getMainController())
        );
    }

    @Test
    void testControllerIndependence() {
        DigLibApp testApp = new DigLibApp(mockHttpClient);
        
        UploadController upload1 = (UploadController) testApp.createController(UploadController.class);
        UploadController upload2 = (UploadController) testApp.createController(UploadController.class);
        ViewController view1 = (ViewController) testApp.createController(ViewController.class);
        ViewController view2 = (ViewController) testApp.createController(ViewController.class);
        ListController list1 = (ListController) testApp.createController(ListController.class);
        ListController list2 = (ListController) testApp.createController(ListController.class);
        
        assertAll("Controller independence",
            () -> assertNotSame(upload1, upload2, "Upload controllers should be different instances"),
            () -> assertNotSame(view1, view2, "View controllers should be different instances"),
            () -> assertNotSame(list1, list2, "List controllers should be different instances")
        );
    }

    private static class TestableDigLibApp extends DigLibApp {
        private final MainController mainController;
        private final UploadController uploadController;
        private final ViewController viewController;
        private final ListController listController;

        public TestableDigLibApp(HttpClient httpClient) throws IOException, InterruptedException {
            super(httpClient);
            this.mainController = new MainController(httpClient);
            this.uploadController = new UploadController();
            this.viewController = new ViewController();
            this.listController = new ListController();
            
            // Pre-initialize controllers
            uploadController.setMainController(mainController, httpClient);
            viewController.setMainController(mainController, httpClient);
            listController.setMainController(mainController);
        }

        @Override
        protected Object createController(Class<?> type) {
            if (type == MainController.class) return mainController;
            if (type == UploadController.class) return uploadController;
            if (type == ViewController.class) return viewController;
            if (type == ListController.class) return listController;
            return super.createController(type);
        }

        public MainController getMainController() {
            return mainController;
        }
    }
}

