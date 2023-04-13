/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validator;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import java.util.Objects;
import javafx.scene.control.Label;

/**
 *
 * @author sof
 */
public class ValidateTF {

    public static boolean isEmpty(JFXTextField textF) {
        boolean empty = false;
        if (textF.getText().trim().equals("")) {
            empty = true;
            textF.getStyleClass().add("wrong-credentials");
        }else{
            empty = false;
            textF.getStyleClass().remove("wrong-credentials");
        }
        return empty;

    }
    public static boolean isEmpty(JFXPasswordField textF) {
        boolean empty = false;
        if (textF.getText().trim().equals("")) {
            empty = true;
            textF.getStyleClass().add("wrong-credentials");
        }else{
            empty = false;
            textF.getStyleClass().remove("wrong-credentials");
        }
        return empty;

    }

    public static boolean isEmpty(JFXTextField textF, Label label, String desc) {
        boolean empty = false;
        if (textF.getText().trim().equals("")) {
            empty = true;
            textF.getStyleClass().add("wrong-credentials");
            if (Objects.nonNull(label)) {
                label.setText(desc);
                label.setVisible(true);
            }

        } else {
            empty = false;
            textF.getStyleClass().remove("wrong-credentials");
            label.setVisible(false);
        }
        return empty;

    }

    public static boolean isSup(JFXTextField textF, double solde) {
        boolean empty = false;
        if (Double.valueOf(textF.getText()) > solde) {
            empty = true;
            textF.getStyleClass().add("wrong-credentials");

        }
        return empty;

    }

    public static boolean isSup(JFXTextField textF, double solde, Label label, String desc) {
        boolean empty = false;
        if (Double.valueOf(textF.getText()) > solde) {
            empty = true;
            textF.getStyleClass().add("wrong-credentials");
            label.setText(desc);
            label.setVisible(true);
        }
        return empty;

    }

    public static boolean isNull(JFXComboBox<String> cb, Label label, String desc) {
        boolean empty = false;

        if (cb.getSelectionModel().getSelectedIndex() == -1) {
            empty = true;
            cb.getStyleClass().add("wrong-credentials");
            label.setText(desc);
            label.setVisible(true);
        } else {
            empty = false;
            cb.getStyleClass().remove("wrong-credentials");
            label.setVisible(false);
        }

        return empty;
    }

}
