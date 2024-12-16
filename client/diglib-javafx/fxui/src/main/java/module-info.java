module fxui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires model;
    requires java.net.http;
    requires javafx.base;

    requires jdk.crypto.ec;
    requires jdk.crypto.cryptoki;
    
    opens fxui to javafx.fxml, javafx.graphics;
    opens fxui.controllers to javafx.fxml;
    
    exports fxui;
    exports fxui.controllers;
    
    uses model.BookHandler;
    uses model.ContentHandler;
    uses model.FileCreator;
    uses model.GenreReader;
}
