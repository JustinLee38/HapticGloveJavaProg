package com.homelab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.fazecast.jSerialComm.SerialPort;

import arduino.Arduino;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class Controller implements Initializable {
    private static final String csvFile = "test.csv";
    private static final String csvSep = ",";

    public ListView<Shape> shapeListView;
    public Label topLabel;
    public Button selectButton;
    public Slider sliderA;
    public Label labelA;
    public Slider sliderB;
    public Label labelB;
    public Slider sliderC;
    public Label labelC;
    public Slider sliderD;
    public Label labelD;
    public TextField saveField;
    public Button saveButton;
    public Button sendButton;
    public TextField text1;
    public TextField text2;
    public TextField text3;
    public TextField text4;
    

    private ObservableList<Shape> shapeList;
    private static Arduino ArduinoCon;
    String comPort;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	SerialPort serials[] = SerialPort.getCommPorts(); 
    	for (SerialPort serial : serials) { 
    		System.out.println(serial.getSystemPortName());
    		comPort = serial.getSystemPortName();
    	}
    	
    	ArduinoCon= new Arduino(comPort , 9600);
    	
    	ArduinoCon.openConnection();
        /*  whenever connection is estabilished the adruino is restarted, we need to wait for it
            otherwise it wont listen
         */
         
        try {
        	
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setListeners();
        loadShapes();
    }    

    public void selectButtonAction(ActionEvent evt) {
        Shape selectedItem = shapeListView.getSelectionModel().getSelectedItem();
        sliderA.setValue(selectedItem.getaValue());
        sliderB.setValue(selectedItem.getbValue());
        sliderC.setValue(selectedItem.getcValue());
        sliderD.setValue(selectedItem.getdValue());
        System.out.println(selectedItem.toString());

    }
    
    public void sendButtonAction(ActionEvent evt) {
    	System.out.println("hello world from send button");
    	System.out.print(Long.toString(Math.round(sliderA.getValue())) + "a,");
    	System.out.print(Long.toString(Math.round(sliderB.getValue())) + "b,");
    	System.out.print(Long.toString(Math.round(sliderC.getValue())) + "c,");
    	System.out.println(Long.toString(Math.round(sliderD.getValue())) + "d");

    	ArduinoCon.serialWrite(Long.toString(Math.round(sliderA.getValue())) + "a");
    	ArduinoCon.serialWrite(Long.toString(Math.round(sliderB.getValue())) + "b");
    	ArduinoCon.serialWrite(Long.toString(Math.round(sliderC.getValue())) + "c");
    	ArduinoCon.serialWrite(Long.toString(Math.round(sliderD.getValue())) + "d"); 
    	

    }

    public void saveButtonAction(ActionEvent evt) {
        String shapeName = "";
        Shape moreShape;

        if ((shapeName = validateNameInput(saveField.getText())) == null) {
            return;
        } else {
            moreShape = new Shape(shapeName, Math.round(sliderA.getValue()), Math.round(sliderB.getValue()), Math.round(sliderC.getValue()), Math.round(sliderD.getValue()));
            shapeList.add(moreShape);
            writeToCSV(moreShape);
            System.out.println(moreShape);
        }
    }

    private boolean writeToCSV(Shape toWrite) {
    	Alert alert;
        StringBuilder csvStr = new StringBuilder();

        csvStr.append(toWrite.getName()).append(",")
                .append(toWrite.getaValue()).append(",")
                .append(toWrite.getbValue()).append(",")
                .append(toWrite.getcValue()).append(",")
                .append(toWrite.getdValue()).append("\n");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            bw.write(csvStr.toString());
        } catch (IOException e) {
        	 alert = new Alert(Alert.AlertType.ERROR, "Error occured while writing to file.\nPlease try again.", ButtonType.OK);
             alert.show();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String validateNameInput(String input) {
        Alert alert;

        if (input == null || "".equals(input)) {
            alert = new Alert(Alert.AlertType.ERROR, "Please enter another name.", ButtonType.OK);
            alert.show();
            return null;
        } else {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
    }

    private boolean loadShapes() {
        List<Shape> rList;

        if ((rList = readCSV()) != null) {
            shapeList = FXCollections.observableList(rList);
        } else {
            shapeList = null;
            return false;
        }

        shapeListView.setItems(shapeList);
        shapeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        shapeListView.setCellFactory(new Callback<ListView<Shape>, ListCell<Shape>>() {
            @Override
            public ListCell<Shape> call(ListView<Shape> param) {
                return new ListCell<Shape>() {
                    @Override
                    protected void updateItem(Shape item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || item.getName() == null || "".equals(item.getName())) {
                            setText("");
                        } else {
                            setText(item.getName());
                        }

                    }
                };
            }
        });

        return true;
    }

    private List<Shape> readCSV() {
        List<Shape> readShapes = new ArrayList<>();
        String readLine;

        File csv = new File(csvFile);

        if (csv.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
                while ((readLine = br.readLine()) != null) {
                    String[] csvRecord = readLine.split(csvSep);
                    readShapes.add(new Shape(csvRecord[0], Long.parseLong(csvRecord[1]), Long.parseLong(csvRecord[2]), Long.parseLong(csvRecord[3]), Long.parseLong(csvRecord[4])));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                csv.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return readShapes;
    }
    
    private void setListeners() {
    sliderA.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            text1.setText(Long.toString(newValue.longValue()));
        }
    });

    sliderB.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            text2.setText(Long.toString(newValue.longValue()));
        }
    });

    sliderC.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            text3.setText(Long.toString(newValue.longValue()));
        }
    });

    sliderD.valueProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            text4.setText(Long.toString(newValue.longValue()));
        }
    });
}
}
