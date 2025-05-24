package com.example.cab302tailproject.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TextFormatting {
    //<editor-fold desc="Text formatting">
    /**
     * Formats the given text content by applying bold styling to the portions of
     * the text enclosed between double asterisks (**). Content outside the markers
     * remains unstyled.
     *
     * @param content the input text containing double asterisks to indicate portions
     *                of the text to be bolded. Text outside the markers will not
     *                have bold styling applied.
     * @return a TextFlow object containing the formatted text with bold styling
     *         applied to the portions specified by the markers.
     */
    public static TextFlow formatTextWithBold(String content) {
        // Create a TextFlow to render styled text
        TextFlow textFlow = new TextFlow();

        // Regular Expression to find text between `**`
        String regex = "\\*\\*(.*?)\\*\\*";
        String[] parts = content.split(regex);

        // Add non-bold text
        for (String part : parts) {
            Text normalText = new Text(part);
            textFlow.getChildren().add(normalText);

            int startIdx = content.indexOf(part) + part.length();
            if (startIdx < content.length() && content.startsWith("**", startIdx)) {
                int endIdx = content.indexOf("**", startIdx + 2) + 2;
                if (endIdx >= 0) {
                    String boldTextStr = content.substring(startIdx + 2, endIdx - 2); // Remove `**`
                    Text boldText = new Text(boldTextStr);
                    boldText.setStyle("-fx-font-weight: bold;");
                    textFlow.getChildren().add(boldText);
                    content = content.substring(endIdx); // Update remaining content
                }
            }
        }
        return textFlow;
    }
    //</editor-fold>


    /**
     * Binds the current system time to a JavaFX Label, formatting the time according to the specified time format.
     * The label's text is updated every second to display the current time.
     *
     * @param label      the JavaFX Label to which the current time will be bound and displayed.
     * @param timeFormat the format pattern for the time, specified as a string compatible with {@link DateTimeFormatter}.
     */
    public static void bindTimeToLabel(Label label, String timeFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);

        // Update the label's text periodically
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = LocalTime.now().format(formatter);
            label.setText("Time " + currentTime);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
