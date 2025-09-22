package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JPanel countryPanel = new JPanel();
            countryPanel.setLayout(new GridLayout(0, 2));
            countryPanel.add(new JLabel("Country:"), 0);
            Translator translator = new JSONTranslator();

            String[] items = new String[translator.getCountryCodes().size()];

            JList<String> list = new JList<>(items);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            CountryCodeConverter countryConverter = new CountryCodeConverter();
            int i = 0;
            for (String countryCode : translator.getCountryCodes()) {
                items[i++] = countryConverter.fromCountryCode(countryCode);
            }
            list.setListData(items);

            JScrollPane scrollPane = new JScrollPane(list);
            countryPanel.add(scrollPane, 1);

            list.addListSelectionListener(new ListSelectionListener() {

                /**
                 * Called whenever the value of the selection changes.
                 *
                 * @param e the event that characterizes the change.
                 */
                @Override
                public void valueChanged(ListSelectionEvent e) {

                    int[] indices = list.getSelectedIndices();
                    String[] items = new String[indices.length];
                    for (int i = 0; i < indices.length; i++) {
                        items[i] = list.getModel().getElementAt(indices[i]);
                    }

                    JOptionPane.showMessageDialog(null, "User selected:" +
                            System.lineSeparator() + Arrays.toString(items));

                }
            });

            JPanel languagePanel = new JPanel();
            JTextField languageField = new JTextField(10);
            languagePanel.add(new JLabel("Language:"));

            LanguageCodeConverter converter = new LanguageCodeConverter();
            JComboBox<String> languageComboBox = new JComboBox<>();
            JSONTranslator jsonTranslator = new JSONTranslator();
            for (String code : jsonTranslator.getLanguageCodes()) {
                languageComboBox.addItem(converter.fromLanguageCode(code));
            }
            languagePanel.add(languageComboBox);

            JPanel translatePanel = new JPanel();
            JLabel resultLabelText = new JLabel("Translation:");
            translatePanel.add(resultLabelText);
            JLabel resultLabel = new JLabel("\t\t\t\t\t\t\t");
            translatePanel.add(resultLabel);

            Runnable update = () -> {
                String languageName = (String) languageComboBox.getSelectedItem();
                String countryName = list.getSelectedValue();
                if (languageName == null || countryName == null) return;

                String languageCode = converter.fromLanguage(languageName);
                String alpha3 = countryConverter.fromCountry(countryName);

                String out = translator.translate(
                        alpha3 == null ? null : alpha3.toLowerCase(),
                        languageCode == null ? null : languageCode.toLowerCase()
                );
                resultLabel.setText(out == null ? "no translation found!" : out);
            };

            languageComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        // String country = languageComboBox.getSelectedItem().toString();
                        update.run();
                    }
                }
            });

            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    update.run();
                }
            });

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(translatePanel);
            mainPanel.add(countryPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            if (list.getModel().getSize() > 0) {
                list.setSelectedIndex(0);
            }
            if (languageComboBox.getItemCount() > 0) {
                languageComboBox.setSelectedIndex(0);
            }
            update.run();
        });
    }
}
