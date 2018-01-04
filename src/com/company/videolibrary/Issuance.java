package com.company.videolibrary;

import java.io.Serializable;

/**
 * Хранит информацию о том, кому выдан диск. <br>
 * {@link Issuance#name} - имя <br>
 * {@link Issuance#surname} - фамилия <br>
 * {@link Issuance#phonenumber} - телефонный номер
 */
public class Issuance implements Serializable {
    /**
     * Имя человека, которому выдан диск
     */
    private String name;
    /**
     * Фамилия человека, которму выдан диск
     */
    private String surname;
    /**
     * Телефонный номер человека, которому выдан диск
     */
    private String phonenumber;

    /**
     * Инициализирует поля {@link Issuance#name}, {@link Issuance#surname} и {@link Issuance#phonenumber}.
     *
     * @param name        имя
     * @param surname     фамилия
     * @param phonenumber телефонный номер
     */
    public Issuance(String name, String surname, String phonenumber) {
        this.name = name;
        this.surname = surname;
        this.phonenumber = phonenumber;
    }

    /**
     * Устанавливает новое значение поля {@link Issuance#name}.
     *
     * @param name имя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает новое значение поля {@link Issuance#surname}.
     *
     * @param surname фамилия
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Устанавливает новое значение поля {@link Issuance#phonenumber}.
     *
     * @param phonenumber телефонный номер
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * Возвращает значение поля {@link Issuance#name}.
     *
     * @return имя (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает значение поля {@link Issuance#surname}.
     *
     * @return фамилия (String)
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Возвращает значение поля {@link Issuance#phonenumber}.
     *
     * @return телефонный номер (String)
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * Возвращает true, если значения полей {@link Issuance#name}, {@link Issuance#surname} и
     * {@link Issuance#phonenumber} совпадают. False - если хоть одно из значений полей не совпало.
     *
     * @param obj объект для сравнения
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Issuance) {
            Issuance i = (Issuance) obj;
            return name.equals(i.name) && surname.equals(i.surname) && phonenumber.equals(i.phonenumber);
        } else return false;
    }


    @Override
    public String toString() {
        return surname + " " + name + " " + phonenumber;
    }
}
