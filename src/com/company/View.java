package com.company;

import com.company.videolibrary.Disk;
import com.company.videolibrary.Issuance;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

//КОПИРОВАНИЕ ДАННЫХ С ДРУГОГО СЕРВЕРА

public class View extends JFrame {

    private JPanel rootPanel;
    /**
     * Текстовое поле русского названия фильма
     */
    private JTextField textRusTitle;
    /**
     * Текстовое поле английского названия фильма
     */
    private JTextField textEngTitle;
    /**
     * Текстовое поле года выпуска фильма
     */
    private JTextField textReleaseYear;
    /**
     * Текстовое поле фамилии человека, которому выдан диск
     */
    private JTextField textSurname;
    /**
     * Текстовое поле имени человека, которому выдан диск
     */
    private JTextField textName;
    /**
     * Текстовое поле номера телефона человека, которому выдан диск
     */
    private JTextField textPhonenumber;
    /**
     * Текстовое поле поиска
     */
    private JTextField textSearch;
    /**
     * Список выданных дисков
     */
    private JList<Disk> issuedDiskList;
    /**
     * Список невыданных дисков
     */
    private JList<Disk> unissuedDiskList;
    /**
     * Кнопка обновления списка дисков
     */
    private JButton updateButton;
    /**
     * Кнопка удаления диска
     */
    private JButton deleteButton;
    /**
     * Кнопка редактирования диска
     */
    private JButton editButton;
    /**
     * Кнопка выдачи диска
     */
    private JButton issueButton;
    /**
     * Выбор способа сортировки
     */
    private JComboBox sortingСomboBox;
    /**
     * Локальная коллекция дисков
     */
    private ArrayList<Disk> diskList;
    /**
     * Поток выходных данных
     */
    private ObjectInputStream in;
    /**
     * Поток выходных данных
     */
    private ObjectOutputStream out;
    /**
     * Код способа сортировки
     */
    private int sortingBy;
    
    private DefaultListModel<Disk> issuedModel = new DefaultListModel<>();
    private DefaultListModel<Disk> unissuedModel = new DefaultListModel<>();
    private boolean isConnected;


    /**
     * Поток-слушатель команд, высылаемых с сервера
     */
    class EchoThread extends Thread {

        @Override
        public void run() {
            try {
                for (; ; ) {
                    Message message = (Message) in.readObject();

                    switch (message.getCode()) {
                        case 1: {
                            diskList = (ArrayList<Disk>) message.getArg(0);
                            break;
                        }
                        case 2: {
                            int index = diskList.indexOf((Disk) message.getArg(0));
                            diskList.remove(index);
                            break;
                        }
                        case 3: {
                            int index = diskList.indexOf((Disk) message.getArg(0));
                            diskList.get(index).removeIssuance();
                            break;
                        }
                        case 4: {
                            int index = diskList.indexOf((Disk) message.getArg(0));
                            Disk disk = diskList.get(index);
                            disk.setRusTitle((String) message.getArg(1));
                            disk.setEngTitle((String) message.getArg(2));
                            disk.setReleaseYear((int) message.getArg(3));
                            break;
                        }
                        case 5: {
                            int index = diskList.indexOf((Disk) message.getArg(0));
                            diskList.get(index).setIssuance((Issuance) message.getArg(1));
                            break;
                        }
                        case 6: {
                            diskList.add((Disk) message.getArg(0));
                            break;
                        }
                        case 7: {
                            diskList.addAll((ArrayList<Disk>) message.getArg(0));
                            break;
                        }
                    }
                    updateDiskLists();
                    editButton.setEnabled(false);
                }
            } catch (SocketException e) {
                System.out.println("Поток завершен");
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    View() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        sortingСomboBox.addItem("Русское название фильма");
        sortingСomboBox.addItem("Английское название фильма");
        sortingСomboBox.addItem("Год выпуска фильма");
        sortingСomboBox.addItem("Русское название фильма (обратн.)");
        sortingСomboBox.addItem("Английское название фильма (обратн.)");
        sortingСomboBox.addItem("Год выпуска фильма (обратн.)");
        sortingBy = sortingСomboBox.getSelectedIndex();

        Integer[] ports = {7070, 7071, 7072, 7073, 7074};
        while (!isConnected) {
            Object port = JOptionPane.showInputDialog(null, "Выберите порт подключения", "Подключение к серверу",
                    JOptionPane.QUESTION_MESSAGE, null, ports, ports[0]);

            if (port != null) connectToServer((Integer) port);
        }


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
                issueButton.setEnabled(true);
                issueButton.setText("Вернуть диск");
                editButton.setEnabled(false);
                deleteButton.setEnabled(true);

                textRusTitle.setEnabled(true);
                textEngTitle.setEnabled(true);
                textReleaseYear.setEnabled(true);
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
                issueButton.setEnabled(true);
                issueButton.setText("Выдать диск");
                editButton.setEnabled(false);
                deleteButton.setEnabled(true);

                textRusTitle.setEnabled(true);
                textEngTitle.setEnabled(true);
                textReleaseYear.setEnabled(true);
            }
        });


        //========================================== Button ActionListeners ==========================================


        deleteButton.addActionListener(e -> {
            try {
                Object[] options = {"Да", "Нет"};
                int result = JOptionPane.showOptionDialog(null, "Удалить диск?", "Подтверждение",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (result == 0) {
                    Disk disk;

                    if (!unissuedDiskList.isSelectionEmpty())
                        disk = unissuedModel.get(unissuedDiskList.getSelectedIndex());
                    else disk = issuedModel.get(issuedDiskList.getSelectedIndex());

                    out.writeObject(new Message(2, disk));
                    out.reset();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            issueButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        });

        updateButton.addActionListener(e -> {
            try {
                out.writeObject(new Message(1));
                out.reset();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        editButton.addActionListener(e -> {

            Disk disk;
            if (!unissuedDiskList.isSelectionEmpty()) disk = unissuedModel.get(unissuedDiskList.getSelectedIndex());
            else disk = issuedModel.get(issuedDiskList.getSelectedIndex());

            try {
                int year = Integer.parseInt(textReleaseYear.getText());
                if (year < 1800 || year > Calendar.getInstance().get(Calendar.YEAR) + 10)
                    throw new NumberFormatException();

                out.writeObject(new Message(4, disk, textRusTitle.getText(), textEngTitle.getText(), year));
                out.reset();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Введено некорректное значение года выпуска", "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            updateDiskLists();
            editButton.setEnabled(false);
            issueButton.setEnabled(false);
        });

        issueButton.addActionListener(e -> {

            if (!unissuedDiskList.isSelectionEmpty()) {

                Disk disk = unissuedModel.get(unissuedDiskList.getSelectedIndex());

                JTextField surname = new JTextField();
                JTextField name = new JTextField();
                JTextField phonenumber = new JTextField();
                JComponent[] inputs = new JComponent[]{
                        new JLabel("Фамилия"), surname,
                        new JLabel("Имя"), name,
                        new JLabel("Телефонный номер"), phonenumber
                };
                Object[] options = {"Выдать", "Отмена"};
                int result = JOptionPane.showOptionDialog(null, inputs, "Выдача диска",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                if (result == 0) {
                    if (surname.getText().equals("") && name.getText().equals("") && phonenumber.getText().equals("")) {
                        JOptionPane.showMessageDialog(null,
                                "Информация о выдаче диска не была заполнена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {
                            Issuance issuance = new Issuance(name.getText(), surname.getText(), phonenumber.getText());
                            out.writeObject(new Message(5, disk, issuance));
                            out.reset();

                            editButton.setEnabled(false);
                            issueButton.setEnabled(false);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else {
                try {
                    Disk disk = issuedModel.get(issuedDiskList.getSelectedIndex());
                    out.writeObject(new Message(3, disk));
                    out.reset();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                editButton.setEnabled(false);
                issueButton.setEnabled(false);
                deleteButton.setEnabled(false);
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

        textSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchDiskLists(textSearch.getText().toLowerCase());
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                issueButton.setEnabled(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchDiskLists(textSearch.getText().toLowerCase());
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                issueButton.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchDiskLists(textSearch.getText().toLowerCase());
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                issueButton.setEnabled(false);
            }
        });


        //================================================= JMenuBar =================================================


        ActionListener menuChoiceListener = event -> {
            switch (event.getActionCommand()) {
                case "Подключиться к серверу": {
                    Object result = JOptionPane.showInputDialog(null, "Выберите порт подключения", "Подключение к серверу",
                            JOptionPane.QUESTION_MESSAGE, null, ports, ports[0]);

                    if (result != null) connectToServer((Integer) result);
                    break;
                }
                case "Отключиться от сервера": {
                    try {
                        out.writeObject(new Message(-1));
                        out.reset();
                        JOptionPane.showMessageDialog(null,
                                "Клиент отключен от сервера.", "Соединение разорвано", JOptionPane.PLAIN_MESSAGE);
                        diskList.clear();
                        updateDiskLists();

                        updateButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        editButton.setEnabled(false);
                        issueButton.setEnabled(false);
                        isConnected = false;
                        setTitle("Домашняя видеотека");

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
                case "Добавить диск": {
                    JTextField rusTitle = new JTextField();
                    JTextField engTitle = new JTextField();
                    JTextField yearOfRelease = new JTextField();
                    JComponent[] inputs = new JComponent[]{
                            new JLabel("Русское название фильма"), rusTitle,
                            new JLabel("Английское название фильма"), engTitle,
                            new JLabel("Год выпуска"), yearOfRelease
                    };
                    Object[] options = {"Добавить", "Отмена"};
                    int result = JOptionPane.showOptionDialog(null, inputs, "Добавление диска",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (result == 0) {
                        if (rusTitle.getText().equals("") && engTitle.getText().equals("") && yearOfRelease.getText().equals("")) {
                            JOptionPane.showMessageDialog(null,
                                    "Информация о выдаче диска не была заполнена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            try {
                                int year = Integer.parseInt(yearOfRelease.getText());
                                if (year < 1800 || year > Calendar.getInstance().get(Calendar.YEAR) + 10)
                                    throw new NumberFormatException();

                                out.writeObject(new Message(6, new Disk(rusTitle.getText(), engTitle.getText(), year)));
                                out.reset();
                                editButton.setEnabled(false);

                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null,
                                        "Введено некорректное значение года выпуска", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
                case "Скопировать данные с другого сервера": {
                    Object port = JOptionPane.showInputDialog(null, "Выберите порт сервера, с которого будут скопированые данные",
                            "Подключение к серверу", JOptionPane.QUESTION_MESSAGE, null, ports, ports[0]);
                    if (port != null) {
                        try {
                            Socket socket = new Socket("localhost", (Integer) port);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                            outputStream.writeObject(new Message(1));
                            outputStream.reset();

                            Message message = (Message) inputStream.readObject();
                            ArrayList<Disk> disks = (ArrayList<Disk>) message.getArg(0);

                            out.writeObject(new Message(7, disks));
                            out.reset();

                        } catch (IOException | ClassNotFoundException e) {
                            JOptionPane.showMessageDialog(null,
                                    "Не удалось подключиться к серверу", "Соединение разорвано", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
                }
            }
        };

        JMenuBar menuBar = new JMenuBar();
        JMenu serverMenu = new JMenu("Сервер");
        JMenu addMenu = new JMenu("Добавить");

        JMenuItem item = new JMenuItem("Подключиться к серверу");
        item.addActionListener(menuChoiceListener);
        serverMenu.add(item);
        serverMenu.add(new JSeparator());

        item = new JMenuItem("Отключиться от сервера");
        item.addActionListener(menuChoiceListener);
        serverMenu.add(item);

        item = new JMenuItem("Добавить диск");
        item.addActionListener(menuChoiceListener);
        addMenu.add(item);
        addMenu.add(new JSeparator());

        item = new JMenuItem("Скопировать данные с другого сервера");
        item.addActionListener(menuChoiceListener);
        addMenu.add(item);

        menuBar.add(serverMenu);
        menuBar.add(addMenu);
        setJMenuBar(menuBar);


        //=========================================== Close Window Listener ==========================================


        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (isConnected) {
                        out.writeObject(new Message(-1));
                        out.reset();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        sortingСomboBox.addActionListener(e -> {
            sortingBy = sortingСomboBox.getSelectedIndex();
            updateDiskLists();
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            issueButton.setEnabled(false);
        });

        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 625));
        setMaximumSize(new Dimension(850, 625));
        setLocation(300, 170);
        setVisible(true);
    }

    /**
     * Выполняет подключение к серверу с номером порта port,
     * инициализирует потоки {@link View#in}, {@link View#out} и
     * запускает новый поток-слушатель {@link EchoThread}
     *
     * @param port номер порта сервера
     */

    private void connectToServer(int port) {
        try {
            Socket socket = new Socket("localhost", port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(new Message(1));
            out.reset();

            EchoThread echoThread = new EchoThread();
            echoThread.start();

            updateButton.setEnabled(true);
            isConnected = true;
            setTitle("Домашняя видеотека (port #" + port + ")");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Не удалось подключиться к серверу", "Соединение разорвано", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Очищает текстовые поля <br>{@link View#textRusTitle}
     * <br>{@link View#textEngTitle}
     * <br>{@link View#textReleaseYear}
     * <br>{@link View#textSurname}
     * <br>{@link View#textName}
     * <br>{@link View#textPhonenumber}
     */
    private void clearTextFields() {
        textRusTitle.setText("");
        textEngTitle.setText("");
        textReleaseYear.setText("");
        textSurname.setText("");
        textName.setText("");
        textPhonenumber.setText("");
    }


    /**
     * Обновляет списки дисков
     * {@link View#issuedDiskList} и
     * {@link View#unissuedDiskList}
     * выборкой из {@link View#diskList}
     */
    private void updateDiskLists() {
        issuedModel.clear();
        unissuedModel.clear();

        ArrayList<Disk> issued = new ArrayList<>();
        ArrayList<Disk> unissued = new ArrayList<>();

        for (Disk d : diskList)
            if (d.isIssued()) issued.add(d);
            else unissued.add(d);

        Comparator<Disk> comparator = getComparator();
        issued.sort(comparator);
        unissued.sort(comparator);

        for (Disk d : issued) issuedModel.addElement(d);
        for (Disk d : unissued) unissuedModel.addElement(d);
        issuedDiskList.setModel(issuedModel);
        unissuedDiskList.setModel(unissuedModel);

        textRusTitle.setEnabled(false);
        textEngTitle.setEnabled(false);
        textReleaseYear.setEnabled(false);
        textSearch.setText("");

        editButton.setEnabled(false);
    }

    /**
     * Обновляет списки дисков
     * {@link View#issuedDiskList} и
     * {@link View#unissuedDiskList}
     * выборкой из {@link View#diskList} по подстроке substring
     *
     * @param substring подстрока для поиска
     */
    private void searchDiskLists(String substring) {
        issuedModel.clear();
        unissuedModel.clear();

        ArrayList<Disk> issued = new ArrayList<>();
        ArrayList<Disk> unissued = new ArrayList<>();

        for (Disk d : diskList)
            if (d.toString().toLowerCase().contains(substring)) {
                if (d.isIssued()) issued.add(d);
                else unissued.add(d);
            }

        Comparator<Disk> comparator = getComparator();
        issued.sort(comparator);
        unissued.sort(comparator);

        for (Disk d : issued) issuedModel.addElement(d);
        for (Disk d : unissued) unissuedModel.addElement(d);
        issuedDiskList.setModel(issuedModel);
        unissuedDiskList.setModel(unissuedModel);
    }

    /**
     * Возвращает компаратор в зависимости от текущей настройки сортировки
     * @return Comparator
     */
    private Comparator<Disk> getComparator() {
        switch (sortingBy) {
            case 0: {
                return new Comparator<Disk>() {
                    @Override
                    public int compare(Disk d1, Disk d2) {
                        return d1.getRusTitle().compareTo(d2.getRusTitle());
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return super.equals(obj);
                    }
                };
            }
            case 1: {
                return new Comparator<Disk>() {
                    @Override
                    public int compare(Disk d1, Disk d2) {
                        return d1.getEngTitle().compareTo(d2.getEngTitle());
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return super.equals(obj);
                    }
                };
            }
            case 2: {
                return new Comparator<Disk>() {
                    @Override
                    public int compare(Disk d1, Disk d2) {
                        return Integer.compare(d1.getReleaseYear(), d2.getReleaseYear());
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return super.equals(obj);
                    }
                };
            }
            case 3: {
                return new Comparator<Disk>() {
                    @Override
                    public int compare(Disk d1, Disk d2) {
                        return -d1.getRusTitle().compareTo(d2.getRusTitle());
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return super.equals(obj);
                    }
                };
            }
            case 4: {
                return new Comparator<Disk>() {
                    @Override
                    public int compare(Disk d1, Disk d2) {
                        return -d1.getEngTitle().compareTo(d2.getEngTitle());
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return super.equals(obj);
                    }
                };
            }
            case 5: {
                return new Comparator<Disk>() {
                    @Override
                    public int compare(Disk d1, Disk d2) {
                        return -Integer.compare(d1.getReleaseYear(), d2.getReleaseYear());
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return super.equals(obj);
                    }
                };
            }
            default:
                return null;
        }
    }

}
