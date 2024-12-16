module model {
    requires transitive java.net.http;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    exports model;

    opens model to com.fasterxml.jackson.databind;
}