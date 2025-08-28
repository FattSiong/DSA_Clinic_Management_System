/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author hangg
 */
public final class ShowMessage {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ShowMessage() {
    }

    /**
     * Displays a standard success message.
     *
     * @param parent The parent component (usually 'this' from the calling frame).
     * @param message The success message to display.
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a general information message with a custom title.
     *
     * @param parent The parent component.
     * @param message The message to display.
     * @param title The title for the dialog window.
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a standard warning message.
     *
     * @param parent The parent component.
     * @param message The warning message to display.
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays a standard error message.
     *
     * @param parent The parent component.
     * @param message The error message to display.
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
