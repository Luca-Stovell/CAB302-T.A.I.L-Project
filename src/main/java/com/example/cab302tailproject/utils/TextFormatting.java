package com.example.cab302tailproject.utils;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
}
