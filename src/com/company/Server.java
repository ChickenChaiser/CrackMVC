package com.company;

import com.company.videolibrary.Disk;
import com.company.videolibrary.Issuance;
import com.company.videolibrary.VideoLibrary;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Параллельный сервер
 */
public class Server {

    /**
     * Видеотека
     */
    private static VideoLibrary videoLibrary = new VideoLibrary();

    public static void main(String ards[]) {

        //============================================= ПОТОК СЕРВЕРА =============================================

        ArrayList<ObjectOutputStream> outputStreams = new ArrayList<>();

        class ServerThread extends Thread {

            private Socket client;

            private ServerThread(Socket client) {
                this.client = client;
            }

            @Override
            public void run() {
                try {
                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                    outputStreams.add(out);

                    while (!client.isClosed()) {

                        Message message = (Message) in.readObject();

                        switch (message.getCode()) {

                            //Запрос данных видеотеки
                            case 1: {
                                out.writeObject(new Message(1, videoLibrary.getDiskList()));
                                break;
                            }

                            //Удаление диска
                            case 2: {
                                int index = videoLibrary.getDiskIndex((Disk) message.getArg(0));
                                videoLibrary.removeDisk(index);
                                for (ObjectOutputStream o : outputStreams)
                                    o.writeObject(message);
                                break;
                            }

                            //Возврат диска
                            case 3: {
                                int index = videoLibrary.getDiskIndex((Disk) message.getArg(0));
                                videoLibrary.unissueDisk(index);

                                for (ObjectOutputStream o : outputStreams)
                                    o.writeObject(message);
                                break;
                            }

                            //Изменение диска
                            case 4: {
                                int index = videoLibrary.getDiskIndex((Disk) message.getArg(0));
                                Disk disk = videoLibrary.getDisk(index);
                                disk.setRusTitle((String) message.getArg(1));
                                disk.setEngTitle((String) message.getArg(2));
                                disk.setReleaseYear((int) message.getArg(3));

                                for (ObjectOutputStream o : outputStreams)
                                    o.writeObject(message);
                                break;
                            }

                            //Выдача диска
                            case 5: {
                                int index = videoLibrary.getDiskIndex((Disk) message.getArg(0));
                                videoLibrary.getDisk(index).setIssuance((Issuance) message.getArg(1));

                                for (ObjectOutputStream o : outputStreams)
                                    o.writeObject(message);
                                break;
                            }

                            //Добавление диска
                            case 6: {
                                videoLibrary.addDisk((Disk) message.getArg(0));
                                for (ObjectOutputStream o : outputStreams)
                                    o.writeObject(message);
                                break;
                            }

                            //Добавление дисков
                            case 7: {
                                ArrayList<Disk> uniqueDisks = videoLibrary.addDisks((ArrayList<Disk>) message.getArg(0));
                                Message m = new Message(7, uniqueDisks);
                                for (ObjectOutputStream o : outputStreams)
                                    o.writeObject(m);
                                break;
                            }

                            //Клиент отключен
                            case -1: {
                                outputStreams.remove(out);
                                in.close();
                                out.close();
                                client.close();
                                System.out.println("\nСоединение закрыто.\n");
                                break;
                            }
                        }
                        if (message.getCode() != -1) out.reset();
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


        //============================================= ЗАПУСК СЕРВЕРА =============================================


        try {
            Integer[] ports = {7070, 7071, 7072, 7073, 7074};

            Object port = JOptionPane.showInputDialog(null, "Выберите порт для запуска сервера", "Запуск сервера",
                    JOptionPane.QUESTION_MESSAGE, null, ports, ports[0]);

            if (port != null) {

                ServerSocket server = new ServerSocket((Integer) port);

                russifyUI();
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true);

                if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {

                    videoLibrary.loadDiskList(new FileInputStream(fileChooser.getSelectedFile().getName()));

                    JOptionPane.showMessageDialog(null,
                            "Сервер запущен", "Запуск сервера", JOptionPane.PLAIN_MESSAGE);

                    for (; ; ) {
                        System.out.print("Ожидание соединения... ");
                        Socket client = server.accept();
                        ServerThread thread = new ServerThread(client);
                        thread.start();
                        System.out.println("Соединение установено.");
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Не удалось запустить сервер. Порт занят.", "Запуск сервера", JOptionPane.ERROR_MESSAGE);
        }
    }



    //============================================= РУССИФИКАТОР =============================================

    /**
     * Руссификация интерфейса FileChooser
     */
    private static void russifyUI() {
        UIManager.put("FileChooser.byNameText", "Имя");
        UIManager.put("FileChooser.byDateText", "Дата изменения");
        UIManager.put("FileChooser.newFolderTitleText", "Новая папка");
        UIManager.put("FileChooser.newFolderButtonText", "Новая папка");
        UIManager.put("FileChooser.untitledFolderName", "новая папка");
        UIManager.put("FileChooser.newFolderPromptText", "Создать папку:");
        UIManager.put("FileChooser.createButtonText", "Создать");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");
        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.newFolderExistsErrorText", "Это имя уже занято!");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.newFolderToolTipText", "Создание новой папки");
        UIManager.put("FileChooser.lookInLabelText", "Текущая папка: ");
        UIManager.put("FileChooser.openButtonText", "Открыть");
    }
}
