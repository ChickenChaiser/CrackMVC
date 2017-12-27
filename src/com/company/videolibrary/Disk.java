package com.company.videolibrary;

import java.io.Serializable;

/**
 * Хранит информацию о DVD-диске домашней видеотеки и о том, кому данный диск выдан. <br>
 * {@link Disk#rusTitle} - русское название фильма <br>
 * {@link Disk#engTitle} - английское название фильма <br>
 * {@link Disk#releaseYear} - год выпуска фильма <br>
 * {@link Disk#issuance} - кому выдан диск
 */
public class Disk implements Serializable {
    /**
     * Русское название фильма
     */
    private String rusTitle;
    /**
     * Английское название фильма
     */
    private String engTitle;
    /**
     * Год выхода
     */
    private int releaseYear;
    /**
     * Кому выдан
     */
    private Issuance issuance;

    /**
     * Инициализирует поля {@link Disk#rusTitle}, {@link Disk#engTitle} и {@link Disk#releaseYear}.
     * Полю {@link Disk#issuance} присваивается значение null (только что созданный диск никому не выдан)
     *
     * @param rusTitle    русское название фильма
     * @param engTitle    английское название фильма
     * @param releaseYear год выпуска фильма
     */
    public Disk(String rusTitle, String engTitle, int releaseYear) {
        this.rusTitle = rusTitle;
        this.engTitle = engTitle;
        this.releaseYear = releaseYear;
        issuance = null;
    }

    /**
     * Устанавливает новое значение поля {@link Disk#rusTitle}.
     *
     * @param rusTitle русское название фильма
     */
    public void setRusTitle(String rusTitle) {
        this.rusTitle = rusTitle;
    }

    /**
     * Устанавливает новое значение поля {@link Disk#engTitle}.
     *
     * @param engTitle английское название фильма
     */
    public void setEngTitle(String engTitle) {
        this.engTitle = engTitle;
    }

    /**
     * Устанавливает новое значение поля {@link Disk#releaseYear}.
     *
     * @param releaseYear год выпуска фильма
     */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Устанавливает новое значение поля {@link Disk#issuance}.
     *
     * @param issuance кому выдан диск
     */
    public void setIssuance(Issuance issuance) {
        this.issuance = issuance;
    }

    /**
     * Возвращает значение поля {@link Disk#rusTitle}.
     *
     * @return русское название фильма (String)
     */
    public String getRusTitle() {
        return rusTitle;
    }

    /**
     * Возвращает значение поля {@link Disk#engTitle}.
     *
     * @return английское название фильма (String)
     */
    public String getEngTitle() {
        return engTitle;
    }

    /**
     * Возвращает значение поля {@link Disk#releaseYear}.
     *
     * @return год выпуска фильма (int)
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Возвращает значение поля {@link Disk#issuance}.
     *
     * @return {@link Disk#issuance}
     */
    public Issuance getIssuance() {
        return issuance;
    }

    /**
     * Возвращает false, если диск никому не выдан ({@link Disk#issuance} == null), иначе - true.
     *
     * @return boolean
     */
    public boolean isIssued() {
        if (issuance == null) return false;
        else return true;
    }

    /**
     * Удаляет информацию о выдаче диска.
     */
    public void removeIssuance() {
        issuance = null;
    }

    /**
     * Возвращает true, если значения полей {@link Disk#rusTitle}, {@link Disk#engTitle} и {@link Disk#releaseYear}
     * совпадают. False - если хоть одно из значений полей не совпало.
     *
     * @param obj диск для сравнения
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        Disk d = (Disk) obj;
        if (rusTitle.equals(d.rusTitle) && engTitle.equals(d.engTitle) && releaseYear == d.releaseYear) return true;
        else return false;
    }

    /**
     * Возвращает строковое представление объекта {@link Disk} в виде "{@link Disk#rusTitle} ({@link Disk#engTitle})
     * {@link Disk#releaseYear}", если у фильма присутсвует английское название. Иначе - "{@link Disk#rusTitle} {@link Disk#releaseYear}"
     *
     * @return String
     */
    @Override
    public String toString() {
        if (engTitle.equals("")) return rusTitle + " " + releaseYear;
        else return rusTitle + " (" + engTitle + ") " + releaseYear;
    }
}
