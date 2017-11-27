package com.company;

import com.company.videolibrary.Disk;
import com.company.videolibrary.Issuance;
import com.company.videolibrary.VideoLibrary;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.LocalGregorianCalendar;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;

public class View extends JFrame {

    private JPanel rootPanel;
    private JTextField textRusTitle;
    private JTextField textEngTitle;
    private JTextField textReleaseYear;
    private JTextField textSurname;
    private JTextField textName;
    private JTextField textPhonenumber;
    private JList<Disk> issuedDiskList;
    private JList<Disk> unissuedDiskList;
    private JButton returnButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton issueButton;
    private VideoLibrary videoLibrary;

    public View(VideoLibrary videoLibrary) {

        this.videoLibrary = videoLibrary;

        DefaultListModel<Disk> issuedModel = new DefaultListModel<>();
        DefaultListModel<Disk> unissuedModel = new DefaultListModel<>();

        for (Disk d : videoLibrary)
            if (d.isIssued()) issuedModel.addElement(d);
            else unissuedModel.addElement(d);

        issuedDiskList.setModel(issuedModel);
        unissuedDiskList.setModel(unissuedModel);


        //========================================= DiskList ActionListeners =========================================


        issuedDiskList.addListSelectionListener(e -> {
            if (issuedDiskList.isSelectionEmpty()) clearTextFields();
            else {
                unissuedDiskList.clearSelection();

                Disk disk = issuedDiskList.getSelectedValue();
                textRusTitle.setText(disk.getRusTitle());
                textEngTitle.setText(disk.getEngTitle());
                textReleaseYear.setText(disk.getReleaseYear() + "");

                if (disk.isIssued()) {
                    textSurname.setText(disk.getIssuance().getSurname());
                    textName.setText(disk.getIssuance().getName());
                    textPhonenumber.setText(disk.getIssuance().getPhonenumber());
                } else {
                    textSurname.setText("");
                    textName.setText("");
                    textPhonenumber.setText("");
                }

                returnButton.setEnabled(true);
                issueButton.setEnabled(false);
                editButton.setEnabled(false);
                deleteButton.setEnabled(true);
            }
        });

        unissuedDiskList.addListSelectionListener(e -> {
            if (unissuedDiskList.isSelectionEmpty()) clearTextFields();
            else {
                issuedDiskList.clearSelection();

                Disk disk = unissuedDiskList.getSelectedValue();
                textRusTitle.setText(disk.getRusTitle());
                textEngTitle.setText(disk.getEngTitle());
                textReleaseYear.setText(Integer.toString(disk.getReleaseYear()));

                if (disk.isIssued()) {
                    textSurname.setText(disk.getIssuance().getSurname());
                    textName.setText(disk.getIssuance().getName());
                    textPhonenumber.setText(disk.getIssuance().getPhonenumber());
                } else {
                    textSurname.setText("");
                    textName.setText("");
                    textPhonenumber.setText("");
                }

                returnButton.setEnabled(false);
                issueButton.setEnabled(true);
                editButton.setEnabled(false);
                deleteButton.setEnabled(true);
            }
        });


        //========================================== Button ActionListeners ==========================================


        deleteButton.addActionListener(e -> {
            if (!unissuedDiskList.isSelectionEmpty()) {
                VideoLibrary.removeDisk(unissuedModel.get(unissuedDiskList.getSelectedIndex()));
                unissuedModel.remove(unissuedDiskList.getSelectedIndex());
            } else if (!issuedDiskList.isSelectionEmpty()) {
                VideoLibrary.removeDisk(issuedModel.get(issuedDiskList.getSelectedIndex()));
                issuedModel.remove(issuedDiskList.getSelectedIndex());
            }
            returnButton.setEnabled(false);
            issueButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        });

        returnButton.addActionListener(e -> {
            Disk disk = issuedModel.get(issuedDiskList.getSelectedIndex());
            disk.removeIssuance();

            issuedModel.remove(issuedDiskList.getSelectedIndex());
            unissuedModel.addElement(disk);

            unissuedDiskList.setSelectedIndex(unissuedModel.size() - 1);
        });

        editButton.addActionListener(e -> {

            Disk disk;
            if (!unissuedDiskList.isSelectionEmpty()) {
                disk = unissuedModel.get(unissuedDiskList.getSelectedIndex());
                disk.setRusTitle(textRusTitle.getText());
                disk.setEngTitle(textEngTitle.getText());
                try {
                    int year = Integer.parseInt(textReleaseYear.getText());
                    if (year < 1800 || year > Calendar.getInstance().get(Calendar.YEAR) + 1) throw new NumberFormatException();
                    disk.setReleaseYear(year);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Введено некорректное значение года выпуска.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

                if (!textSurname.getText().equals("") || !textName.getText().equals("") || !textPhonenumber.getText().equals("")) {
                    disk.setIssuance(new Issuance(textName.getText(), textSurname.getText(), textPhonenumber.getText()));
                    unissuedModel.remove(unissuedDiskList.getSelectedIndex());
                    issuedModel.addElement(disk);
                }
            } else {
                disk = issuedModel.get(issuedDiskList.getSelectedIndex());
                disk.setRusTitle(textRusTitle.getText());
                disk.setEngTitle(textEngTitle.getText());
                try {
                    int year = Integer.parseInt(textReleaseYear.getText());
                    if (year < 1800 || year > Calendar.getInstance().get(Calendar.YEAR) + 1) throw new NumberFormatException();
                    disk.setReleaseYear(year);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Введено некорректное значение года выпуска.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

                if (textSurname.getText().equals("") && textName.getText().equals("") && textPhonenumber.getText().equals("")) {
                    disk.removeIssuance();
                    issuedModel.remove(issuedDiskList.getSelectedIndex());
                    unissuedModel.addElement(disk);
                } else {
                    disk.getIssuance().setSurname(textSurname.getText());
                    disk.getIssuance().setName(textName.getText());
                    disk.getIssuance().setPhonenumber(textPhonenumber.getText());
                }
            }
            issuedDiskList.setModel(issuedModel);
            unissuedDiskList.setModel(unissuedModel);
            editButton.setEnabled(false);
        });

        issueButton.addActionListener(e -> {
            if (!unissuedDiskList.isSelectionEmpty()) {
                int index = unissuedDiskList.getSelectedIndex();
                Disk disk = unissuedModel.get(index);

                JTextField surname = new JTextField();
                JTextField name = new JTextField();
                JTextField phonenumber = new JTextField();
                JComponent[] inputs = new JComponent[]{
                        new JLabel("Фамилия"), surname,
                        new JLabel("Имя"), name,
                        new JLabel("Телефонный номер"), phonenumber
                };
                int result = JOptionPane.showConfirmDialog(null, inputs, "Выдача диска",
                        JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null);

                if (result != -1) {
                    if (surname.getText().equals("") && name.getText().equals("") && phonenumber.getText().equals("")) {
                        JOptionPane.showMessageDialog(null,
                                "Информация о выдаче диска не была заполнена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } else {
                        disk.setIssuance(new Issuance(name.getText(), surname.getText(), phonenumber.getText()));
                        unissuedModel.remove(index);
                        issuedModel.addElement(disk);
                        issuedDiskList.setModel(issuedModel);
                        unissuedDiskList.setModel(unissuedModel);
                    }
                }
            }
        });

        //========================================= TextField ActionListeners =========================================


        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                editButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                editButton.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        textRusTitle.getDocument().addDocumentListener(documentListener);
        textEngTitle.getDocument().addDocumentListener(documentListener);
        textReleaseYear.getDocument().addDocumentListener(documentListener);
        textSurname.getDocument().addDocumentListener(documentListener);
        textName.getDocument().addDocumentListener(documentListener);
        textPhonenumber.getDocument().addDocumentListener(documentListener);


        ActionListener menuChoiceListener = event -> {
            /*switch (event.getActionCommand()) {
                case "Open dwelling": {
                    type = "Dwelling";
                    Buildings.setCurrentFactory(new DwellingFactory());
                    break;
                }
                case "Open office building": {
                    type = "Office building";
                    Buildings.setCurrentFactory(new DwellingFactory());
                    break;
                }
            }*/
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showDialog(this, "Выберите файл");

            if (result == JFileChooser.APPROVE_OPTION) {
                try (FileReader file = new FileReader(fileChooser.getSelectedFile())) {
                    //initFrames(Buildings.readBuilding(file), type);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(this, exception);
                }
            }
        };

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenu addMenu = new JMenu("Добавить");

        JMenuItem item = new JMenuItem("Открыть файл видеотеки");
        item.addActionListener(menuChoiceListener);
        fileMenu.add(item);
        fileMenu.add(new JSeparator());

        item = new JMenuItem("Сохранить");
        item.addActionListener(menuChoiceListener);
        fileMenu.add(item);
        //fileMenu.add(new JSeparator());

        item = new JMenuItem("Сохранить как...");
        item.addActionListener(menuChoiceListener);
        fileMenu.add(item);

        item = new JMenuItem("Добавить новый диск");
        item.addActionListener(menuChoiceListener);
        addMenu.add(item);
        addMenu.addSeparator();

        item = new JMenuItem("Загрузить из файла видеотеки");
        item.addActionListener(menuChoiceListener);
        addMenu.add(item);

        menuBar.add(fileMenu);
        menuBar.add(addMenu);
        setJMenuBar(menuBar);

        setTitle("Домашняя видеотека");
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 580));
        setMaximumSize(new Dimension(850, 580));
        setLocation(300, 170);
        setVisible(true);
    }

    private void clearTextFields() {
        textRusTitle.setText("");
        textEngTitle.setText("");
        textReleaseYear.setText("");
        textSurname.setText("");
        textName.setText("");
        textPhonenumber.setText("");
    }
}
