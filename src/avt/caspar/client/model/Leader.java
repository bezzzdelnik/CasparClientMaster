package avt.caspar.client.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

public class Leader {
    private StringProperty id;
    private StringProperty name;
    private StringProperty flag;
    private StringProperty time;
    private StringProperty handicap;
    private BooleanProperty visible;


    public Leader(StringProperty id, StringProperty name, StringProperty flag, StringProperty time, StringProperty handicap, BooleanProperty visible) {
        this.id = id;
        this.name = name;
        this.flag = flag;
        this.time = time;
        this.handicap = handicap;
        this.visible = visible;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getFlag() {
        return flag.get();
    }

    public StringProperty flagProperty() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag.set(flag);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getHandicap() {
        return handicap.get();
    }

    public StringProperty handicapProperty() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap.set(handicap);
    }

    public boolean isVisible() {
        return visible.get();
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }
}
