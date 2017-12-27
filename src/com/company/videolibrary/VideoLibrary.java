package com.company.videolibrary;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Обработчик данных домашней видотеки
 */
public class VideoLibrary implements Iterable<Disk> {
    /**
     * Хранилище дисков
     */
    private static ArrayList<Disk> diskList = new ArrayList<>();

    /**
     * Сохраняет видеотеку в файл
     *
     * @param out файл для сохранения
     */
    public void writeDiskList(FileOutputStream out) throws IOException {
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(diskList);
    }

    /**
     * Загружает видеотеку из файла
     *
     * @param in файл видеотеки
     */
    public void loadDiskList(FileInputStream in) {
        try {
            ObjectInputStream objectIn = new ObjectInputStream(in);
            diskList = (ArrayList<Disk>) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            diskList.clear();
        }
    }

    /**
     * Загружает дополнительные данные видеотеки из файла in
     *
     * @param in файл видеотеки
     */
    public void addDataFromFile(FileInputStream in) {
        try {
            ObjectInputStream objectIn = new ObjectInputStream(in);
            ArrayList<Disk> data = (ArrayList<Disk>) objectIn.readObject();
            for (Disk d : data) {
                if (!diskList.contains(d)) diskList.add(d);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает количество дисков в видеотеке
     *
     * @return количество дисков (int)
     */
    public int getNumberOfDisks() {
        return diskList.size();
    }

    /**
     * Возвращает диск, хранимый в видеотеке
     *
     * @param index индекс диска в видеотеке
     * @return Disk
     */
    public Disk getDisk(int index) {
        return diskList.get(index);
    }

    /**
     * Добавляет диск в видеотеку
     *
     * @param disk диск для добавления
     */
    public void addDisk(Disk disk) {
        diskList.add(disk);
    }

    /**
     * Заменяет диск в видеотеке
     *
     * @param index индекс заменяемого диска
     * @param disk  новый диск
     */
    public void setDisk(int index, Disk disk) {
        diskList.set(index, disk);
    }

    /**
     * Удаляет диск из видеотеки
     *
     * @param position индекс удаляемого диска
     * @throws IndexOutOfBoundsException диска с таким индексом не существует
     */
    public void removeDisk(int position) {
        if (position >= 0 && position < getNumberOfDisks()) diskList.remove(position);
        else throw new IndexOutOfBoundsException("Диск не найден");
    }

    /**
     * Удаляет диск из видеотеки
     *
     * @param disk удаляемый диск
     * @throws NullPointerException диск не найден
     * @see VideoLibrary#removeDisk(int)
     */
    public void removeDisk(Disk disk) {
        if (disk != null) diskList.remove(disk);
        else throw new NullPointerException("Диск не найден");
    }

    /**
     * Возвращает итератор по дискам домашней библиотеки
     *
     * @return iterator
     */
    @Override
    public Iterator<Disk> iterator() {
        return new Iterator<Disk>() {
            private int index = -1;

            @Override
            public boolean hasNext() {
                return index < diskList.size() - 1;
            }

            @Override
            public Disk next() {
                index++;
                return diskList.get(index);
            }
        };
    }
}
