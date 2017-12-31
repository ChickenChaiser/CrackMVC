package com.company;

import java.io.Serializable;
import com.company.videolibrary.Disk;
import com.company.videolibrary.Issuance;

/**
 * Унифицированное сообщение, предназначенное для общения клиента и сервера <br>
 * {@link Message#code} - код запрашиваемой операции <br>
 * {@link Message#args} - аргументы операции <br>
 */
public class Message implements Serializable{

    /**
     * Код запрашиваемой операции. Может принимать следующие значения в паре с соответствующими аргументами: <br><br>
     *     1 - Запрос данных видеотеки (нет аргументов) <br>
     *     2 - Удалить диск ({@link Message#args}[0] - {@link Disk}) <br>
     *     3 - Вернуть диск ({@link Message#args}[0] - {@link Disk}) <br>
     *     4 - Изменить диск ({@link Message#args}[0] - {@link Disk}, {@link Message#args}[1] - String newRusTitle,
     *     {@link Message#args}[2] - String newEngTitle, {@link Message#args}[3] - newReleaseYear) <br>
     *     5 - Выдать диск ({@link Message#args}[0] - {@link Disk}, {@link Message#args}[1] - {@link Issuance}) <br>
     *     6 - Добавить диск ({@link Message#args}[0] - {@link Disk}) <br>
     *     7 - Добавить диски ({@link Message#args}[0] - ArrayList<{@link Disk}>) <br>
     *     -1 - Разорвать соединение (нет аргументов)
     */
    private int code;

    /**
     * Аргументы операции
     */
    private Object[] args;

    /**
     * Инициализирует поля {@link Message#code} и {@link Message#args}.
     *
     * @param command  код запрашиваемой операции
     * @param args     аргументы переменной длины
     */
    Message(int command, Object... args) {
        this.code = command;
        this.args = args;
    }

    /**
     * Возвращает код операции {@link Message#code}
     *
     * @return код операции (int)
     */
    int getCode() {
        return code;
    }

    /**
     * Возвращает массив аргументов операции {@link Message#args}
     *
     * @return массив аргументов операции (Object[])
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Возвращает аргумент операции {@link Message#args}[index]
     *
     * @param index индекс аргумента
     *
     * @return аргумент операции (Object)
     */
    Object getArg(int index) {
        return args[index];
    }
}
