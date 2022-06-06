package com.komar_olga.cloud_gb;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HelloController implements Initializable {

    @FXML
    private HBox cloudBox;
    @FXML
    private VBox clientBox, serverBox;
    @FXML
    private RadioButton downloadRadio, uploadRadio, deleteRadio, renameRadio;
    @FXML
    private TableView<FileData> filesListClient, filesListServer;
    @FXML
    private TableColumn<FileData, String> filesNameClient, filesTypeClient, filesSizeClient, filesNameServer, filesTypeServer, filesSizeServer;
    @FXML
    private Label textSelectedRadio;
    @FXML
    private Button buttonRadio;
    @FXML
    private TextField addressBarClient, addressBarServer;

    private ObservableList<FileData> clientData = FXCollections.observableArrayList();
    private ObservableList<FileData> serverData = FXCollections.observableArrayList();
    private String indicator = "client";

    @Override

    public void initialize(URL location, ResourceBundle resources) {


//        Thread t = new Thread(() -> {
//            try {
//                AbstractMessage am = Network.readObject();
//                if (am instanceof FileMessage) {
//                    FileMessage fm = (FileMessage) am;
//                    if (fm.getActionPoint().equals("download")) {
//                        Files.write(Paths.get("client_storage/" + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
//                        System.out.println("ddd");
//                    }
//                }
//                if (am instanceof FileList) {
//                    FileList fl = (FileList) am;
//                    //System.out.println(fl.getFileName().size());
//                    for (int i = 0; i < fl.getFileName().size(); i++) {
////                        System.out.print(fl.getFileName().get(i));
////                        System.out.print("." + fl.getFileType().get(i));
////                        System.out.println(" [" + fl.getFileSize().get(i) + "]");
//                        serverData.add(new FileData(fl.getFileName().get(i), fl.getFileType().get(i), fl.getFileSize().get(i) + " Byte"));
//                    }
//
//                }
//            } catch (ClassNotFoundException | IOException e) {
//                e.printStackTrace();
//            } finally {
//                //todo если убрать коменты сразу закрывает соединение
//               // Network.stop();
//            }
//        });
//        t.setDaemon(true);
//        t.start();
        try {
            Network.start();
            refreshClientData();
            refreshServerData();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //todo Проблемы с остановкой сервера
            //Network.stop();
        }
        filesListClient.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {
            @Override
            public void changed(ObservableValue<? extends FileData> observable, FileData oldValue, FileData newValue) {
                if (filesListClient.getSelectionModel().getSelectedItem() != null) {
                    String address = filesListClient.getSelectionModel().getSelectedItem().getName() + "." + filesListClient.getSelectionModel().getSelectedItem().getType();
                    addressBarClient.setText(address);
                }
            }
        });
        filesListServer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {
            @Override
            public void changed(ObservableValue<? extends FileData> observable, FileData oldValue, FileData newValue) {
                if (filesListServer.getSelectionModel().getSelectedItem() != null) {
                    String address = filesListServer.getSelectionModel().getSelectedItem().getName() + "." + filesListServer.getSelectionModel().getSelectedItem().getType();
                    addressBarServer.setText(address);
                }
            }
        });

    }

    private void refreshServerData() {
        Network.sendMsg(new FileRequest(null, "list"));
        try {
            AbstractMessage am = Network.readObject();
            if (am instanceof FileList) {
                FileList fl = (FileList) am;

                for (int i = 0; i < fl.getFileName().size(); i++) {
                    serverData.add(new FileData(fl.getFileName().get(i), fl.getFileType().get(i), fl.getFileSize().get(i) + " Byte"));
                }
            }
        }catch (ClassNotFoundException|IOException e){
            e.printStackTrace();
     }

        filesNameServer.setCellValueFactory(new PropertyValueFactory<FileData, String>("name"));
        filesTypeServer.setCellValueFactory(new PropertyValueFactory<FileData, String>("type"));
        filesSizeServer.setCellValueFactory(new PropertyValueFactory<FileData, String>("size"));

        filesListServer.setItems(serverData);

    }

    private void refreshClientData() throws IOException {
        FileList fl = new FileList("client_storage/");
        for (int i = 0; i < fl.getFileName().size(); i++) {
            clientData.add(new FileData(fl.getFileName().get(i), fl.getFileType().get(i), fl.getFileSize().get(i) + " Byte"));
        }
        filesNameClient.setCellValueFactory(new PropertyValueFactory<FileData, String>("name"));
        filesTypeClient.setCellValueFactory(new PropertyValueFactory<FileData, String>("type"));
        filesSizeClient.setCellValueFactory(new PropertyValueFactory<FileData, String>("size"));

        filesListClient.setItems(clientData);

    }

    public void onClickRadioButton() {
        if (downloadRadio.isSelected()) {
            textSelectedRadio.setText("Скачать файл из облака?");
            buttonRadio.setText("Download");
        }
        if (uploadRadio.isSelected()) {
            textSelectedRadio.setText("Загрузить файл в облоко?");
            buttonRadio.setText("Upload");
        }
        if (deleteRadio.isSelected()) {
            textSelectedRadio.setText("Удалить файл?");
            buttonRadio.setText("Delete");
            Font font = new Font("Cambria Bold", 16);
            Label deleteLabel = new Label("    Вы хотите удалить файл на Клиенте или на Сервере?");
            deleteLabel.setFont(font);
            deleteLabel.setWrapText(true);
            Button buttonClient = new Button("На клиенте");
            buttonClient.setFont(font);
            Button buttonServer = new Button("На сервере");
            buttonServer.setFont(font);
            StackPane deletePane = new StackPane();
            deletePane.getChildren().addAll(deleteLabel, buttonClient, buttonServer);
            deletePane.setAlignment(deleteLabel, Pos.TOP_CENTER);
            deletePane.setMargin(deleteLabel, new Insets(20, 10, 0, 10));
            deletePane.setAlignment(buttonClient, Pos.CENTER_LEFT);
            deletePane.setMargin(buttonClient, new Insets(30, 0, 0, 65));
            deletePane.setAlignment(buttonServer, Pos.CENTER_RIGHT);
            deletePane.setMargin(buttonServer, new Insets(30, 65, 0, 0));
            Scene deleteScene = new Scene(deletePane, 400, 200);
            Stage deleteWindow = new Stage();
            deleteWindow.setTitle("Что удаляем?");
            deleteWindow.setResizable(false);
            deleteWindow.setScene(deleteScene);
            deleteWindow.initModality(Modality.WINDOW_MODAL);
            buttonClient.setOnAction(event -> {
                indicator = "client";
                deleteWindow.close();
            });
            buttonServer.setOnAction(event -> {
                indicator = "server";
                deleteWindow.close();
            });
            deleteWindow.show();
        }
        if (renameRadio.isSelected()) {
            textSelectedRadio.setText("Переименовать файл?");
            buttonRadio.setText("Rename");
            Font font = new Font("Cambria Bold", 16);
            Label renameLabel = new Label("    Вы хотите переименовать файл на Клиенте или на Сервере?");
            renameLabel.setFont(font);
            renameLabel.setWrapText(true);
            Button buttonClient = new Button("На клиенте");
            buttonClient.setFont(font);
            Button buttonServer = new Button("На сервере");
            buttonServer.setFont(font);
            StackPane renamePane = new StackPane();
            renamePane.getChildren().addAll(renameLabel, buttonClient, buttonServer);
            renamePane.setAlignment(renameLabel, Pos.TOP_CENTER);
            renamePane.setMargin(renameLabel, new Insets(20, 10, 0, 10));
            renamePane.setAlignment(buttonClient, Pos.CENTER_LEFT);
            renamePane.setMargin(buttonClient, new Insets(30, 0, 0, 65));
            renamePane.setAlignment(buttonServer, Pos.CENTER_RIGHT);
            renamePane.setMargin(buttonServer, new Insets(30, 65, 0, 0));
            Scene renameScene = new Scene(renamePane, 400, 200);
            Stage renameWindow = new Stage();
            renameWindow.setTitle("Что переименовываем?");
            renameWindow.setResizable(false);
            renameWindow.setScene(renameScene);
            renameWindow.initModality(Modality.WINDOW_MODAL);
            buttonClient.setOnAction(event -> {
                indicator = "client";
                renameWindow.close();
            });
            buttonServer.setOnAction(event -> {
                indicator = "server";
                renameWindow.close();
            });
            renameWindow.show();
        }
    }


    public void onClickButton() {
        if (downloadRadio.isSelected()) {
            if (!addressBarServer.getText().equals(null)) {
                Network.sendMsg(new FileRequest(addressBarServer.getText(), "download"));
                filesListClient.getItems().clear();
                try {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        if (fm.getActionPoint().equals("download")) {
                            Files.write(Paths.get("client_storage/" + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        }
                    }
                    refreshClientData();
                } catch (ClassNotFoundException |IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (uploadRadio.isSelected()) {
            if (!addressBarClient.getText().equals(null)) {
                try {
                    Network.sendMsg(new FileMessage(Paths.get("client_storage/" + addressBarClient.getText()), "upload"));
                    filesListServer.getItems().clear();
                    refreshServerData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(addressBarClient.getText());
                addressBarClient.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                alert.showAndWait();
            }

        }
        if (deleteRadio.isSelected()) {
            if (indicator.equals("client")) {
                try {
                    Path path = Paths.get("client_storage/" + addressBarClient.getText());
                    Files.delete(path);
                    filesListClient.getItems().clear();
                    refreshClientData();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                    alert.showAndWait();
                    addressBarClient.clear();
                }
                addressBarClient.clear();
            } else {
                System.out.println(addressBarServer.getText());
                Network.sendMsg(new FileRequest(addressBarServer.getText(), "delete"));
                addressBarServer.clear();
                filesListServer.getItems().clear();
                refreshServerData();


            }
        }
        if (renameRadio.isSelected()) {
            if (indicator.equals("client")) {
                try {
                    String address = filesListClient.getSelectionModel().getSelectedItem().getName() + "."
                            + filesListClient.getSelectionModel().getSelectedItem().getType();
                    Path sourcePath = Paths.get("client_storage/" + address);
                    Path destinationPath = Paths.get("client_storage/" + addressBarClient.getText());
                    System.out.println(sourcePath.getFileName() + "->" + destinationPath.getFileName());
                    Files.move(sourcePath, destinationPath);
                    filesListClient.getItems().clear();
                    refreshClientData();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Не верное действие");
                    alert.showAndWait();
                    addressBarClient.clear();
                }
                addressBarClient.clear();
            } else {
                //todo не верно переименовывыает
                String address = filesListServer.getSelectionModel().getSelectedItem().getName() + "."
                        + filesListServer.getSelectionModel().getSelectedItem().getType();
                Network.sendMsg(new FileRequest(address, "rename_second"));
                try {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileRequest) {
                        FileRequest fr = (FileRequest) am;
                        if (fr.getActionPoint().equals("rename")) {
                            Network.sendMsg(new FileRequest(addressBarServer.getText(), "rename_first"));
                            addressBarServer.clear();
                        }
                    }
                } catch (ClassNotFoundException |IOException e) {
                    e.printStackTrace();
                }

                filesListServer.getItems().clear();
                refreshServerData();
                //addressBarServer.clear();
            }

        }

    }
}