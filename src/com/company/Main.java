package com.company;

import com.company.videolibrary.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
        VideoLibrary.addDisk(new Disk("Побег из Шоушенка", "The Shawshank Redemption", 1994));
        VideoLibrary.addDisk(new Disk("Зелёная миля", "The Green Mile",1999));
        VideoLibrary.addDisk(new Disk("Форрест Гамп", "Forrest Gump",1994));
        VideoLibrary.addDisk(new Disk("Список Шиндлера", "Schindler's List",1993));
        VideoLibrary.addDisk(new Disk("1+1", "Intouchables",2011));
        VideoLibrary.addDisk(new Disk("Начало", "Inception ",2010));
        VideoLibrary.addDisk(new Disk("Король Лев", "The Lion King",1994));
        VideoLibrary.addDisk(new Disk("Леон", "Léon",1994));
        VideoLibrary.addDisk(new Disk("Бойцовский клуб", "Fight Club",1999));
        VideoLibrary.addDisk(new Disk("Иван Васильевич меняет профессию", "",1973));
        VideoLibrary.getDisk(0).setIssuance(new Issuance("Матвей", "Корцев", "+7(937)120-10-77"));
        VideoLibrary.getDisk(1).setIssuance(new Issuance("Артём", "Воронов", "210-50-59"));
        VideoLibrary.getDisk(4).setIssuance(new Issuance("Матвей", "Корцев", "+7(937)120-10-77"));
        VideoLibrary.getDisk(5).setIssuance(new Issuance("Алексей", "Краснобаев", "+7(927)745-24-25"));
        VideoLibrary.getDisk(6).setIssuance(new Issuance("Николай", "Дунюшкин", "227-94-92"));
        VideoLibrary.getDisk(8).setIssuance(new Issuance("Яна", "Концова", "+7(917)175-30-90"));
        try {
            VideoLibrary.writeDiskList(new FileOutputStream("DiskData.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

public class Main {

    public static void main(String[] args) {

        VideoLibrary videoLibrary = null;
        try {
            videoLibrary = new VideoLibrary(new FileInputStream("DiskData.dat"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        View view = new View(videoLibrary);
    }
}
