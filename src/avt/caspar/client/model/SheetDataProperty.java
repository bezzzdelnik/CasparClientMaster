package avt.caspar.client.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SheetDataProperty {

    private final StringProperty sheetData;
    private final StringProperty sheetID;

    private final StringProperty sheetData2;

    public SheetDataProperty() {
        this(null, null, null);
    }


    public SheetDataProperty(String sheetID,String sheetData, String sheetData2) {
        this.sheetData = new SimpleStringProperty(sheetData);
        this.sheetID = new SimpleStringProperty(sheetID);
        this.sheetData2 = new SimpleStringProperty(sheetData2);
    }

    public String getSheetData2() {
        return sheetData2.get();
    }

    public StringProperty sheetData2Property() {
        return sheetData2;
    }

    public void setSheetData2(String sheetData2) {
        this.sheetData2.set(sheetData2);
    }

    public String getSheetData() {
        return sheetData.get();
    }

    public StringProperty sheetDataProperty() {
        return sheetData;
    }

    public void setSheetData(String sheetData) {
        this.sheetData.set(sheetData);
    }

    public String getSheetID() {
        return sheetID.get();
    }

    public StringProperty sheetIDProperty() {
        return sheetID;
    }

    public void setSheetID(String sheetID) {
        this.sheetID.set(sheetID);
    }
}
