package avt.caspar.client.util;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.ArrayList;

public class TableMultiSelections {

    private TableView tableView;
    private DataFormat SERIALIZED_MIME_TYPE;
    private final ArrayList<Object> selectionsSD = new ArrayList<>();

    public TableMultiSelections(TableView tableView, DataFormat dataFormat) {
        this.tableView = tableView;
        this.SERIALIZED_MIME_TYPE = dataFormat;
    }

    public void setSingleSelection() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setMultiSelection() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Перетаскивание строк и обработка двойного нажатия по строке
        tableView.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            //перетаскивание строк
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();

                    selectionsSD.clear();//important...

                    ObservableList<Object> items = tableView.getSelectionModel().getSelectedItems();

                    for (Object iI : items) {
                        selectionsSD.add(iI);
                    }


                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();

                if (db.hasContent(SERIALIZED_MIME_TYPE)) {

                    int dropIndex;
                    Object dI = null;

                    if (row.isEmpty()) {
                        dropIndex = tableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                        dI = tableView.getItems().get(dropIndex);
                    }
                    int delta = 0;
                    if (dI != null)
                        while (selectionsSD.contains(dI)) {
                            delta = 1;
                            --dropIndex;
                            if (dropIndex < 0) {
                                dI = null;
                                dropIndex = 0;
                                break;
                            }
                            dI = tableView.getItems().get(dropIndex);
                        }

                    for (Object sI : selectionsSD) {
                        tableView.getItems().remove(sI);
                    }

                    if (dI != null)
                        dropIndex = tableView.getItems().indexOf(dI) + delta;
                    else if (dropIndex != 0)
                        dropIndex = tableView.getItems().size();


                    tableView.getSelectionModel().clearSelection();

                    for (Object sI : selectionsSD) {
                        //draggedIndex = selections.get(i);
                        tableView.getItems().add(dropIndex, sI);
                        tableView.getSelectionModel().select(dropIndex);
                        dropIndex++;

                    }

                    event.setDropCompleted(true);
                    selectionsSD.clear();
                    event.consume();
                }
            });
            return row;
        });
    }
}
