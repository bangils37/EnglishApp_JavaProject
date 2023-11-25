package dictionary;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import dictionary.core.DatabaseDictionary;
import dictionary.core.Dictionary;
import dictionary.core.Trie;
import dictionary.util.StringUtil;
import javafx.scene.text.Text;

public class TerminalApp {

    private static final int LEFT_PANEL_WIDTH = 36;
    private static final int RIGHT_PANEL_WIDTH = 48;

    private static Dictionary dictionary;
    private static ActionListBox searchResultPanel;
    private static TextBox definitionTextBox;
    private static MultiWindowTextGUI gui;

    public static void main(String[] args) throws IOException, SQLException {
        initializeDictionary();
        Screen screen = createScreen();
        BasicWindow window = createWindow();
        Panel mainPanel = createMainPanel();
        window.setComponent(mainPanel);
        gui.addWindowAndWait(window);
    }

    /**
     * Initialize the dictionary.
     */
    private static void initializeDictionary() {
        dictionary = new DatabaseDictionary();
        try {
            dictionary.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a screen.
     * 
     * @return the screen
     * @throws IOException if an I/O error occurs
     */
    private static Screen createScreen() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLACK));
        return screen;
    }

    /**
     * Create a window.
     * 
     * @return the window
     */
    private static BasicWindow createWindow() {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        window.setTitle("[Dictionary]");
        return window;
    }

    /**
     * Create the main panel.
     * 
     * @return the main panel
     */
    private static Panel createMainPanel() {
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(2));
        Panel sidePanel = createSidePanel();
        Panel wordListPanel = createWordListPanel();
        mainPanel.addComponent(sidePanel);
        mainPanel.addComponent(wordListPanel.withBorder(Borders.singleLine("Definition")));
        return mainPanel;
    }

    /**
     * Create the side panel.
     */
    private static Panel createSidePanel() {
        Panel sidePanel = new Panel();
        sidePanel.addComponent(buildSearchBar().withBorder(Borders.singleLine()));
        searchResultPanel = new ActionListBox();
        searchResultPanel.setPreferredSize(new TerminalSize(LEFT_PANEL_WIDTH, 18));
        sidePanel.addComponent(searchResultPanel.withBorder(Borders.singleLine("Result")));
        Panel toolbarPanel = createToolbarPanel();
        sidePanel.addComponent(toolbarPanel.withBorder(Borders.singleLine("Toolbar")));
        return sidePanel;
    }

    /**
     * Create the toolbar panel.
     * 
     * @return the toolbar panel
     */
    private static Panel createToolbarPanel() {
        Panel toolbarPanel = new Panel();
        toolbarPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        Button addWordButton = createAddWordButton();
        Button deleteWordButton = createDeleteWordButton();

        toolbarPanel.addComponent(addWordButton);
        toolbarPanel.addComponent(deleteWordButton);

        return toolbarPanel;
    }

    /**
     * Create the add word button.
     * 
     * @return the add word button
     */
    private static Button createAddWordButton() {
        Button addWordButton = new Button("Add Word", () -> openAddWordWindow());
        addWordButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER));
        return addWordButton;
    }

    /**
     * Create the delete word button.
     * 
     * @return the delete word button
     */
    private static Button createDeleteWordButton() {
        Button deleteWordButton = new Button("Delete Word", () -> openDeleteWordWindow());
        deleteWordButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER));
        return deleteWordButton;
    }

    /**
     * Open the add word window.
     */
    private static void openAddWordWindow() {
        Window addWordWindow = new BasicWindow();
        addWordWindow.setHints(Arrays.asList(Window.Hint.CENTERED));
        Panel addWordPanel = createAddWordPanel();
        addWordWindow.setComponent(addWordPanel);
        gui.addWindowAndWait(addWordWindow);
    }

    /**
     * Open the delete word window.
     */
    private static void openDeleteWordWindow() {
        Window deleteWordWindow = new BasicWindow();
        deleteWordWindow.setHints(Arrays.asList(Window.Hint.CENTERED));
        Panel deleteWordPanel = createDeleteWordPanel();
        deleteWordWindow.setComponent(deleteWordPanel);
        gui.addWindowAndWait(deleteWordWindow);
    }

    /**
     * Create the delete word panel.
     * 
     * @return the delete word panel
     */
    private static Panel createDeleteWordPanel() {
        Panel deleteWordPanel = new Panel();
        TextBox word = new TextBox(new TerminalSize(32, 1));

        deleteWordPanel.addComponent(new Label("Word:"));
        deleteWordPanel.addComponent(word);

        deleteWordPanel.addComponent(new EmptySpace());

        Panel buttonPanel = createButtonPanel(word);
        deleteWordPanel.addComponent(buttonPanel);

        return deleteWordPanel;
    }

    /*
     * Create the button panel.
     * 
     * @param word the word
     * 
     * @return the button panel
     */
    private static Panel createButtonPanel(TextBox word) {
        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("Delete", () -> {
            handleDeleteWord(
                    word.getText());
            gui.getActiveWindow().close();
        }));
        buttonPanel.addComponent(new Button("Cancel", () -> gui.getActiveWindow().close()));
        return buttonPanel;
    }

    /**
     * Handle the delete word.
     * 
     * @param word the word
     */
    private static void handleDeleteWord(String word) {
        if (word.isEmpty()) {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Word cannot be empty")
                    .build()
                    .showDialog(gui);
            return;
        }

        if (dictionary.deleteWord(word)) {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Word deleted successfully")
                    .build()
                    .showDialog(gui);
        } else {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Failed to delete word")
                    .build()
                    .showDialog(gui);
        }
    }

    /**
     * Create the add word panel.
     * 
     * @return the add word panel
     */
    private static Panel createAddWordPanel() {
        Panel addWordPanel = new Panel();
        TextBox word = new TextBox(new TerminalSize(32, 1));
        TextBox definition = new TextBox(new TerminalSize(32, 16));

        addWordPanel.addComponent(new Label("Word:"));
        addWordPanel.addComponent(word);

        addWordPanel.addComponent(new Label("Definition:"));
        addWordPanel.addComponent(definition);

        addWordPanel.addComponent(new EmptySpace());

        Panel buttonPanel = createButtonPanel(word, definition);
        addWordPanel.addComponent(buttonPanel);

        return addWordPanel;
    }

    /*
     * Create the button panel.
     * 
     * @param word the word
     * 
     * @param definition the definition
     * 
     * @return the button panel
     */
    private static Panel createButtonPanel(TextBox word, TextBox definition) {
        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("Add", () -> {
            handleAddWord(
                    word.getText(),
                    definition.getText());
            gui.getActiveWindow().close();
        }));
        buttonPanel.addComponent(new Button("Cancel", () -> gui.getActiveWindow().close()));
        return buttonPanel;
    }

    /**
     * Create the word list panel.
     * 
     * @return the word list panel
     */
    private static Panel createWordListPanel() {
        Panel wordListPanel = new Panel();
        definitionTextBox = new TextBox(new TerminalSize(RIGHT_PANEL_WIDTH, 24));
        wordListPanel.addComponent(definitionTextBox);
        return wordListPanel;
    }

    /**
     * Build the search bar.
     * 
     * @return the search bar
     */
    private static Panel buildSearchBar() {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        panel.setPreferredSize(new TerminalSize(LEFT_PANEL_WIDTH, 1));

        TextBox textBox = new TextBox(new TerminalSize(LEFT_PANEL_WIDTH, 1));
        Button searchButton = createSearchButton(textBox);
        Button getAllButton = createGetAllButton();

        panel.addComponent(textBox);
        panel.addComponent(searchButton);
        panel.addComponent(getAllButton);

        return panel;
    }

    /**
     * Create the search button.
     * 
     * @param textBox the text box
     * @return the search button
     */
    private static Button createSearchButton(TextBox textBox) {
        return new Button("Search", () -> {
            String searchText = textBox.getText();

            if (searchText.isEmpty()) {
                searchResultPanel.clearItems();
                return;
            }

            List<String> results = Trie.getInstance().search(searchText);
            handleSearchResults(searchText, results);
        });
    }

    /**
     * Handle the search results.
     * 
     * @param searchText the search text
     * @param results    the search results
     */
    private static void handleSearchResults(String searchText, List<String> results) {
        if (results.isEmpty()) {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("No results found for \"" + searchText + "\"")
                    .build()
                    .showDialog(gui);
            return;
        }

        searchResultPanel.clearItems();

        for (String result : results) {
            searchResultPanel.addItem(result, () -> {
                String definition = dictionary.lookUpWord(result);
                definitionTextBox.setText(StringUtil.removeHtmlTags(definition));
            });
        }
    }

    /**
     * Handle the add word.
     * 
     * @param word       the word
     * @param definition the definition
     */
    private static void handleAddWord(String word, String definition) {
        if (word.isEmpty() || definition.isEmpty()) {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Word and definition cannot be empty")
                    .build()
                    .showDialog(gui);
            return;
        }

        if (dictionary.insertWord(word, definition)) {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Word added successfully")
                    .build()
                    .showDialog(gui);
        } else if (dictionary.updateWordDefinition(word, definition)) {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Word updated successfully")
                    .build()
                    .showDialog(gui);
        } else {
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Failed to add word")
                    .build()
                    .showDialog(gui);
        }
    }

    /**
     * Create the get all button.
     * 
     * @return the get all button
     */
    private static Button createGetAllButton() {
        return new Button("All", () -> {
            List<String> allWords = dictionary.getAllWordTargets();

            searchResultPanel.clearItems();

            for (String word : allWords) {
                searchResultPanel.addItem(word, () -> {
                    String definition = dictionary.lookUpWord(word);
                    definitionTextBox.setText(StringUtil.removeHtmlTags(definition));
                });
            }
        });
    }
}