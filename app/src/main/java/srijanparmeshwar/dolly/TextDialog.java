package srijanparmeshwar.dolly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static srijanparmeshwar.dolly.TextDialog.MarkdownParser.State.*;

/**
 * Created by Srijan on 22/04/2017.
 */

public class TextDialog {

    private static final String TAG = "TextDialog";

    public static Dialog create(Activity host, String title, String filename) {
        AssetManager manager = host.getAssets();
        String textHTML = parseFile(manager, filename);

        WebView view = new MarkdownView(host);
        view.loadData("<!DOCTYPE html><html><head></head><body style=\"margin=0.5em;\">" + textHTML + "</body></html>", "text/html", "utf-8");

        AlertDialog.Builder builder = new AlertDialog.Builder(host);
        builder
                .setTitle(title)
                .setView(view);

        return builder.create();
    }

    private static String parseFile(AssetManager manager, String filename) {
        MarkdownParser parser = new MarkdownParser();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(filename), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parser.parse(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return parser.generateHTML();
    }

    private static class MarkdownView extends WebView {
        public MarkdownView(Context context) {
            super(context);
        }
    }

    // Very basic markdown parser.
    static class MarkdownParser {
        private final StringBuilder buffer;
        private State state;

        enum State {
            NONE,
            PARAGRAPH
        }

        private static final String START_PARAGRAPH = "<p>";
        private static final String END_PARAGRAPH = "</p>";
        private static final String NEW_LINE = "<br>";

        public MarkdownParser() {
            buffer = new StringBuilder();
            state = NONE;
        }

        public void parse(String line) {
            if (line.startsWith("#")) {
                switch (state) {
                    case PARAGRAPH:
                        buffer.append(END_PARAGRAPH);
                        buffer.append(NEW_LINE);
                        break;
                    default:
                        break;
                }
                buffer.append(titleToHTML(line));
                buffer.append(NEW_LINE);
                state = NONE;
            } else {
                if (line.length() > 0) {
                    switch (state) {
                        case PARAGRAPH:
                            buffer.append(line);
                            buffer.append(NEW_LINE);
                            break;
                        default:
                            buffer.append(START_PARAGRAPH);
                            buffer.append(NEW_LINE);
                            buffer.append(line);
                            buffer.append(NEW_LINE);
                            state = PARAGRAPH;
                            break;
                    }
                }
            }
        }

        public String generateHTML() {
            switch (state) {
                case PARAGRAPH:
                    buffer.append(END_PARAGRAPH);
                    break;
                default:
                    break;
            }
            return buffer.toString();
        }

        private static String titleToHTML(String input) {
            int index = 1;
            char[] characters = input.toCharArray();
            while (index < characters.length && characters[index] == ' ') {
                index++;
            }
            return "<b>" + input.substring(index) + "</b>";
        }
    }


}
