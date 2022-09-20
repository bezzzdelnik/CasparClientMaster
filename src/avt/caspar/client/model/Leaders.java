package avt.caspar.client.model;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Leaders {
    private StringProperty gender;
    private StringProperty distance;
    private StringProperty title;
    private StringProperty timeTitle;
    private ObservableList<Leader> leadersList = FXCollections.observableArrayList();

    public Leaders(StringProperty gender, StringProperty distance, StringProperty title, StringProperty timeTitle) {
        this.gender = gender;
        this.distance = distance;
        this.title = title;
        this.timeTitle = timeTitle;
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getDistance() {
        return distance.get();
    }

    public StringProperty distanceProperty() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance.set(distance);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getTimeTitle() {
        return timeTitle.get();
    }

    public StringProperty timeTitleProperty() {
        return timeTitle;
    }

    public void setTimeTitle(String timeTitle) {
        this.timeTitle.set(timeTitle);
    }

    public ObservableList<Leader> getLeadersList() {
        return leadersList;
    }

    public void setLeadersList(ObservableList<Leader> leadersList) {
        this.leadersList = leadersList;
    }
}
