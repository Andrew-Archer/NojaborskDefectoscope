/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.main;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import ru.npptmk.sortoscope.model.Diameter;
import ru.npptmk.sortoscope.model.Diameters;
import ru.npptmk.sortoscope.model.DiametersValues;
import ru.npptmk.sortoscope.model.DurabilityGroups;
import ru.npptmk.sortoscope.model.DurabilityGroupsSignals;
import ru.npptmk.sortoscope.util.RevDataInputStream;
import ru.npptmk.sortoscope.util.RevDataOutputStream;

/**
 * Реализация драйвера для сортоскопа 4 производства Кропус.
 * Проверено на версиях прошивок сортоскопа 4.10, 4.20.
 *
 * @author RazumnovAA
 */
public class Sortoscope4Driver implements SortoscopeDriver {

    /**
     * IP адрес соротоскопа.
     */
    private String sortoscopeIP;

    /**
     * Порт который слушает сортоскоп.
     */
    private Integer sortoscopePort;

    /**
     * Если true то выводит логи.
     */
    private boolean isDebugging;

    /**
     * Для форматирования выводимых данных.
     */
    private SimpleDateFormat dateFormater;

    public Sortoscope4Driver(String ip, Integer port) {
        this.sortoscopeIP = ip;
        this.sortoscopePort = port;
        dateFormater = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss.SSS");
        isDebugging = false;
    }

    /**
     * При создании без указания порта и IP адреса драйвер подключается к
     * 192.168.0.199:13120.
     */
    public Sortoscope4Driver() {
        this("192.168.0.199", 13120);
    }

    /**
     * Включает запись в лог отладочной информации.
     */
    public void enableDebuggingLog(){
        isDebugging = true;
    }
    
    /**
     * Выключает запись в лог отладочной информации.
     */
    public void disableDebuggingLog(){
        isDebugging = false;
    }
    /**
     * Позволяет узнать включен ли вывод отладочной информации.
     * 
     * @return true - если вывод отладочной информации включен.
     */
    public boolean isDebugging(){
        return isDebugging;
    }
    /**
     * Получает все текущие настройки сортоскопа для 5 ти диаметров.
     *
     * @return лист настроек для 5 ти диаметров.
     */
    private List<Diameter> getAllDiametersParameters() {
        //Для сохранения настроек всех доступных диаметров,
        //полученных от сортоскопа
        List<Diameter> diametersSettings = new ArrayList<>();

        //Заполняем список настроек пустыми настройками
        for (int i = 0; i < 5; i++) {
            Diameter diameter = new Diameter();
            diameter.setDiameter(DiametersValues.values()[i]);
            diametersSettings.add(diameter);
        }

        //Размер ответа от соротоскопа в байтах
        int responseSize = 512;

        //Номер комманды которая для запроса на сортоскоп
        int commandNumber = 0X33200004;

        //Создаем необходимые ресурсы для отправки и получения данных от
        //сортоскопа.
        try (
                //Создать сокет клиента
                Socket kkSocket = new Socket(sortoscopeIP, sortoscopePort);
                //Создать поток для отправки данных в сортоскоп
                RevDataOutputStream toServer = new RevDataOutputStream(kkSocket.getOutputStream());
                //Создать поток для получения данных из соротоскопа
                RevDataInputStream fromServer = new RevDataInputStream(kkSocket.getInputStream());) {

            //Отправить запрос о получении информации о сортоскопе.
            toServer.writeInt(commandNumber);

            //С помощью этой переменной читаем ответный поток.
            short shortToRead;

            //Прочитать возвращённое сортоскопом сообщение из потока.
            //Сформировать объект настроек для диаметра
            try {
                //Читаем заголов ответа
                fromServer.readShort();

                //Читаем номер трубы
                fromServer.readShort();

                //Читаем текущее значение для калибровки ухх
                fromServer.readShort();

                //Читаем значение текущего диаметра
                //И устанавливаем пометку активного диаметра
                //у соответствующего диаметра в списке.
                diametersSettings.get(fromServer.readShort()).setCurrent(true);

                //Заполняем текущие настройки частот для каждого диаметра
                for (Diameter d : diametersSettings) {
                    d.setMesurmentFrequency(fromServer.readShort());
                }

                //Читаем текущие настройки напряжения холостого хода
                for (Diameter d : diametersSettings) {
                    d.setNoTubeSignal(fromServer.readShort());
                }

                //Читаем попроговые значения групп прочности для каждого диаметра
                for (Diameter d : diametersSettings) {
                    for (DurabilityGroups durGroups : DurabilityGroups.values()) {
                        d.setDurabilityGropSignalValue(durGroups, fromServer.readShort());
                    }
                }

                //Дочитываем остаток сообщения
                while (true) {
                    fromServer.readShort();
                    System.out.println(String.valueOf(fromServer.readShort()));
                }
            } catch (EOFException e) {
                //Данная ошибка это нормальный выход из цикла чтения.
                //Другие варианты менее эффетивные.
            } finally {
                toServer.close();
                fromServer.close();
                kkSocket.close();
            }
            //Поймать и обработать исключения    
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + sortoscopeIP + " "
                    + sortoscopePort);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //Вернуть настройки для всех диаметров
        return diametersSettings;
    }

    /**
     * Возвращает стандартные настройки сортоскопа для указанного диаметра.
     *
     * @param diameter диаметр трубы для которого нужно вернуть стандартные
     * настройки сортоскопа.
     * @return стандартные настройкки сортоскопа для заданного диаметра.
     */
    public Diameter getStandardParameters(DiametersValues diameter) {
        //Создаем настройки сортоскопа для указанного диаметра
        //и инициализируем параметры сортоскопа общие для всех диаметров.
        Diameter diameterToReturn = new Diameter();
        diameterToReturn.setDiameter(diameter);
        diameterToReturn.setMesurmentFrequency((short) 16);
        diameterToReturn.setNoTubeSignal((short) -3000);

        switch (diameter) {
            case D60мм:
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Д, (short) -373);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.К, (short) -472);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Е, (short) -808);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Л, (short) -1027);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.М, (short) -1151);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Р, (short) -1606);
                break;
            case D73мм:
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Д, (short) 14);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.К, (short) -92);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Е, (short) -453);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Л, (short) -688);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.М, (short) -820);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Р, (short) -1309);
                break;
            case D89мм:
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Д, (short) 563);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.К, (short) 469);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Е, (short) 147);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Л, (short) -63);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.М, (short) -181);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Р, (short) -618);
                break;
            case D102мм:
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Д, (short) 877);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.К, (short) 755);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Е, (short) 262);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Л, (short) -2);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.М, (short) -116);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Р, (short) -640);
                break;
            case D114мм:
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Д, (short) 1902);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.К, (short) 1731);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Е, (short) 1056);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Л, (short) 702);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.М, (short) 541);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Р, (short) -208);
                break;
            default:
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Д, (short) 0);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.К, (short) 0);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Е, (short) 0);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Л, (short) 0);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.М, (short) 0);
                diameterToReturn.setDurabilityGropSignalValue(
                        DurabilityGroups.Р, (short) 0);
        }
        return diameterToReturn;
    }

    @Override
    public Diameter getDiameterParameters() {
        //Полчаем настройки для всех параметров
        List<Diameter> allDiametersSettings = getAllDiametersParameters();

        //Получаем диаметр, являющийся текущей настройкой сортоскопа
        // и возвращаем его.
        for (Diameter diameter : allDiametersSettings) {
            if (diameter.isCurrent()) {
                return diameter;
            }
        }
        return null;
    }

    /**
     * Устанавливает списки параметров для всех 5 диаметров.
     *
     * @param diametersParameters список параметров для всех 5 параметров.
     * @param diameterToContorl диаметр который необходимо установить как
     * рабочий.
     */
    private void setAllDiametersParameters(
            List<Diameter> diametersParameters,
            DiametersValues diameterToContorl) throws IOException {
        //Длина данных в количестве слов
        int dataLength = 512;
        //Номер комманды для запроса
        int commandNumber = 0X33200005;

        //Создаем необходимые ресурсы для отправки и получения данных от
        //сортоскопа.
        try (
                //Создать сокет клиента
                Socket kkSocket = new Socket(sortoscopeIP, sortoscopePort);
                //Создать поток для отправки данных в сортоскоп
                RevDataOutputStream toServer = new RevDataOutputStream(kkSocket.getOutputStream());
                //Создать поток для получения данных из соротоскопа
                RevDataInputStream fromServer = new RevDataInputStream(kkSocket.getInputStream());) {

            //Номер текущего диаметра содержится в diametersParameters
            //и записывается в массив parameters.
            //Преобразуем настройки диаметров в массив short
            short[] parameters = Diameters.getAsArrayOfShort(diametersParameters);

            //Отправить номер комманды
            toServer.writeInt(commandNumber);

            //Отправляем индикатор начала параметров
            toServer.writeShort((short) 0XCDDD);

            //Отправляем номер трубы
            toServer.writeShort(0X0001);

            //Отправляем параметры трубы
            for (short aShort : parameters) {
                toServer.writeShort(aShort);
            }

            //Заполняем оставшеесся место 0-ми.
            for (int i = 0; i < 467; i++) {
                toServer.writeShort(0);
            }
            //Читаем ответ сортоскопа
            try {
                System.out.println("Getting response after setting params.");
                for (int i = 0; i < 512; i++) {
                    System.out.println(fromServer.readShort());
                }
            } catch (EOFException e) {
                //Данная ошибка это нормальный выход из цикла чтения.
                //Другие варианты менее эффетивны.
            } finally {
                toServer.close();
                fromServer.close();
                kkSocket.close();
            }
            //Поймать и обработать исключения    
        }
    }

    @Override
    public void setDiameterParameters(Diameter diameter) throws IOException {
        //Список параметров весех 5 диаметров.
        List<Diameter> diameters = new ArrayList<>();

        //Заолняем список настроек для 5 диаметров.
        for (int i = 0; i < 5; i++) {
            //Если i - индекс значимого диаметра
            if (i == diameter.getDiameter().ordinal()) {
                //Добавляем единственный значимый диаметр и его параметры
                //На соответствующее диаметру место в спике.
                diameters.add(diameter);
                diameter.setCurrent(true);
            } else {
                //Добавляем заглушки для не имеющих значения диаметров.
                Diameter newDiameter = new Diameter(DiametersValues.values()[i], (short) 0);
                diameters.add(newDiameter);
            }
        }

        //Отправляет список диаметров и их параметров.
        setAllDiametersParameters(diameters, diameter.getDiameter());
    }

    /**
     * Метод используется для внутренней обработки полученных результатов.
     * Поэтому этот метод не дотупен для внешней программы.
     *
     * @return результаты ввиде массива <tt>short</tt>.
     */
    private Short[] getResults() throws NoMeasurementsReadyException {
        //Номер комманды которая для запроса на сортоскоп
        int commandNumber = 0X33200003;

        //С помощью этой переменной читаем ответный поток.
        Short[] result = new Short[2048];

        //Создаем необходимые ресурсы для отправки и получения данных от
        //сортоскопа.
        try (
                //Создать сокет клиента
                Socket kkSocket = new Socket(sortoscopeIP, sortoscopePort);
                //Создать поток для отправки данных в сортоскоп
                RevDataOutputStream toServer = new RevDataOutputStream(kkSocket.getOutputStream());
                //Создать поток для получения данных из соротоскопа
                RevDataInputStream fromServer = new RevDataInputStream(kkSocket.getInputStream());) {

            //Отправить запрос о получении информации о сортоскопе.
            toServer.writeInt(commandNumber);

            //Прочитать вовращённое сортоскопом сообщение из потока.
            for (int i = 0; i < result.length; i++) {
                result[i] = fromServer.readShort();
            }

            //Проверяем, что прочтенные данные корректны.
            for (int i = 0; i < 4; i++) {
                //Если 0 байт любого из 4 пакетов не 0XCD
                if (result[i * 512] != 0XFFFFCDAA) {
                    throw new NoMeasurementsReadyException(
                            "Данные полученные от сортоскопа имею неправильную структуру. \n"
                            + " (Пакет: " + i + "DWORD: 0) != 0XFFFFCDAA");
                }

                //Если установлен не верный номер пакета
                if (result[i * 512 + 1] != i) {
                    throw new NoMeasurementsReadyException(
                            "Данные полученные от сортоскопа имею неправильную структуру. \n"
                            + " Ожидаемый номер пакета: " + i + " Фактический номер пакета: " + result[i * 512 + 2]);
                }
            }
            //Если количество полученных значений в результате измерений 0
            if (result[3] == 0) {
                throw new NoMeasurementsReadyException(
                        "Результаты измерений пока не готовы.");
            }

            //Поймать и обработать исключения    
        } catch(ConnectException ex){
            JOptionPane.showMessageDialog(null, "Проблема сосединения с сортоскопом.\nПопробуйте перезагрузить сортоскоп.", "Ошибка", ERROR_MESSAGE);
        }catch (UnknownHostException e) {
            System.err.println("Don't know about host " + sortoscopeIP + " "
                    + sortoscopePort);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return result;
    }

    @Override
    public DurabilityGroups getDurabilityGroup() throws NoMeasurementsReadyException {
        //Возвращаемая группа прочночти
        DurabilityGroups durabilityGroups = DurabilityGroups.Р;

        //Полчучаем результаты измерения от сортоскопа
        Short[] resultToProcess = getResults();

        //Для хранения среднего значения замеров.
        short overageValue;

        //Для хранения порогов для групп прочности.
        DurabilityGroupsSignals durabilityGroupsSignals = new DurabilityGroupsSignals();

        //Проверяем наличие результатов измерений.
        //Если количество измерений > 0 то данные есть.
        if (resultToProcess[3] != 0) {
            if (isDebugging) {
                System.out.println(dateFormater.format(new Date()) + "> Количество результатов :" + resultToProcess[3]);
            }
            //Запомнить среднее значение измерений.
            overageValue = resultToProcess[6];
            if (isDebugging) {
                System.out.println(dateFormater.format(new Date()) + "> Среднее значение: " + overageValue);
            }

            //Выводим группы прочности
            if (isDebugging) {
                for (int j = 9; j < 15; j++) {
                    System.out.println(dateFormater.format(new Date()) + "> Порого для группы " + DurabilityGroups.values()[j - 9] + ": " + resultToProcess[j]);
                }
            }

            //По среднему значению результатов замера
            //получить группу прочности.
            //Перебираем пороги групп прочности в возвращенных результатах.
            for (int j = 9; j < 15; j++) {
                //Если среднее занчение больше порога группы
                if (resultToProcess[j] < overageValue) {
                    //Запомнить группу соответствующую среднему значению замеров
                    durabilityGroups = DurabilityGroups.values()[j - 9];
                    break;
                }
            }
            if (isDebugging) {
                System.out.println(dateFormater.format(new Date()) + "> Группа прочности: " + durabilityGroups);
            }
            return durabilityGroups;
        }
        //Нет результатов замеров. Выбросить исключение.
        throw new NoMeasurementsReadyException();
    }

    @Override
    public void getInternalData() {
        //Номер комманды которая для запроса на сортоскоп
        int commandNumber = 0X33200000;

        //Создаем необходимые ресурсы для отправки и получения данных от
        //сортоскопа.
        try (
                //Создать сокет клиента
                Socket kkSocket = new Socket(sortoscopeIP, sortoscopePort);
                //Создать поток для отправки данных в сортоскоп
                RevDataOutputStream toServer = new RevDataOutputStream(kkSocket.getOutputStream());
                //Создать поток для получения данных из соротоскопа
                RevDataInputStream fromServer = new RevDataInputStream(kkSocket.getInputStream());) {

            //Отправить запрос о получении информации о сортоскопе.
            toServer.writeInt(commandNumber);

            //С помощью этой переменной читаем ответный поток.
            short shortToRead;

            //Прочитать вовращённое сортоскопом сообщение из потока.
            try {
                while (true) {
                    shortToRead = fromServer.readShort();
                    System.out.println(String.valueOf(shortToRead));
                }
            } catch (EOFException e) {
                //Данная ошибка это нормальный выход из цикла чтения.
                //Другие варианты менее эффетивные.
            } finally {
                toServer.close();
                fromServer.close();
                kkSocket.close();
            }
            //Поймать и обработать исключения    
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + sortoscopeIP + " "
                    + sortoscopePort);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Ой ёй видимо херовая программа в сортоскопе дает о себе знать.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String getIp() {
        return sortoscopeIP;
    }

    public void setIp(String ip) {
        this.sortoscopeIP = ip;
    }

    public Integer getPort() {
        return sortoscopePort;
    }

    public void setPort(Integer port) {
        this.sortoscopePort = port;
    }

}
