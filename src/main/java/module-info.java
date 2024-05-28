module com.medcure.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.medcure.app to javafx.fxml;
    exports com.medcure.app;
    exports com.medcure.utils;
    opens com.medcure.utils to javafx.fxml;

    // Memberikan akses refleksi ke kelas Medicine
    opens com.medcure.data to javafx.base;
}

