package dictionary;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.AbsoluteLayout;
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
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import dictionary.server.DatabaseDictionary;
import dictionary.server.Dictionary;
import dictionary.server.Trie;

public class TerminalApp {

    public static int LEFT_PANEL_WIDTH = 32;

    public static int RIGHT_PANEL_WIDTH = 32;

    private static Dictionary dictionary;

    private static Panel searchResultPanel;

    private static TextBox definitionTextBox;

    public static void main(String[] args) throws IOException, SQLException {
        dictionary = new DatabaseDictionary();
        dictionary.initialize();

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);

        screen.startScreen();

        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        window.setTitle("Dictionary");

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLACK));

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(2));

        Panel sidePanel = new Panel();

        sidePanel.addComponent(buildSearchBar().withBorder(Borders.singleLine()));

        searchResultPanel = new Panel();
        searchResultPanel.setPreferredSize(new TerminalSize(LEFT_PANEL_WIDTH, 24));

        sidePanel.addComponent(searchResultPanel.withBorder(Borders.singleLine("Result")));

        Panel toolbarPanel = new Panel();

        Button addWordButton = new Button("Add Word");
        addWordButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER));

        toolbarPanel.addComponent(addWordButton);

        sidePanel.addComponent(toolbarPanel.withBorder(Borders.singleLine("Toolbar")));

        Panel wordListPanel = new Panel();
        wordListPanel.addComponent(buildWordList());

        mainPanel.addComponent(sidePanel);
        mainPanel.addComponent(wordListPanel.withBorder(Borders.singleLine("Definition")));
        window.setComponent(mainPanel);
        gui.addWindowAndWait(window);
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
        Button searchButton = new Button("Search", () -> {
            String searchText = textBox.getText();
            List<String> results = Trie.search(searchText);

            int maxWords = searchResultPanel.getSize().getRows();

            // limit the number of results to 10
            if (results.size() > maxWords) {
                results = results.subList(0, maxWords);
            }

            searchResultPanel.removeAllComponents();

            for (String result : results) {
                Button button = new Button(result, () -> {
                    String definition = dictionary.lookUpWord(result);
                    definitionTextBox.setText(definition);
                });
                searchResultPanel.addComponent(button);
            }
        });

        panel.addComponent(textBox);
        panel.addComponent(searchButton);

        return panel;
    }

    private static Panel buildWordList() {
        Panel panel = new Panel();
        panel.setPreferredSize(new TerminalSize(RIGHT_PANEL_WIDTH, 24));

        definitionTextBox = new TextBox(new TerminalSize(RIGHT_PANEL_WIDTH, 24));

        panel.addComponent(definitionTextBox);
        return panel;
    }
}