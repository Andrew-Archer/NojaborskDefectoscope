/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Абстрактный базовый класс для драйверов сканирующих устройств, работающих по
 * протоколу UDP.<br>
 * <br>
 * Общая схема взаимодействия с устройством следующая:<br>
 * <br>
 * От прикладной программы устройству передаются команды в виде UDP пакетов
 * фиксированного размера. В ответ на команду от устройства поступают результаты
 * сканирования или измереия в реальном времени в виде UDP пакетов
 * фиксированного размера. Данные от устройства поступают сериями по несколько
 * пакетов. Количество пакетов в серии (или ее продожительность) определено
 * настройками устройства.
 * <br>
 * Каждый пакет в серии содержит результаты, относящиеся к моменту времени,
 * непосредственно предшествовавшему моменту отправки пакета. Таким образом, все
 * пакеты в серии содержат различные данные, относящиеся к разным моментам
 * времени. Привязка к моменту времени содержится внутри данных пакета
 * (порядковый номер, время и т.д.).<br>
 * Помимо результатов измерения один из ответных пакетов серии может содержаить
 * результат выполнения команды, переданной от прикладной программы устройству,
 * если таковой существует. Если в серии такого пакета нет, значит команда
 * устройством не воспринята.<br>
 * Если новая команда от прикладной программы передается на устройство до
 * завершения передачи устройством ответной серии предыдущей команды, то с
 * момента получения новой команды, устройство начинает передачу новой ответной
 * серии, при этом предыдущая серия завершается, а результаты, полученные после
 * поступления новой команды помещаются в ее ответную серию. Таким образом, для
 * поддержания непрерывного потока результатов измерения необходимо отправлять
 * устройству команды с интервалом времени меньшим, чем продолжительность
 * серии.<br>
 * Драйвер предполагает наличие у устройства двух видов команд: разовые и
 * повторяемые.<br>
 * Разовые команды передаются устройству один раз. Как правило, они предполагают
 * наличие ответной реакции устройства в одном из пакетов ответной серии. Если
 * разовая команда поступила до завершения обработки устройством предыдущей
 * разовой команды и формирования ответной реакции, она будет
 * проигнорирована.<br>
 * Повторяемые команды передаются устройству постоянно с интервалом времени
 * немногим менее продолжительности серии. Как правило, единственной реакцией
 * устройства на такую команду является выдача серии результатов. Эти команды
 * обеспечивают поддержание непрерывного потока результатов измерения в течение
 * продолжительного времени.<br>
 * <br>
 * Устройство производит большое количество измерений (порядка нескольких тысяч
 * в секунду). Такой массив данных не требуется прикладной программе, однако
 * позволяет повысить достоверность результатов измерения путем статистической
 * обработки полученных результатов. Для этого драйвер поддерживает следующий
 * сценарий обработки поступающих данных:<br>
 * Результаты нескольких ответных пакетов (не обязательно всей серии)
 * накапливаются и, по достижении необходимого количества пакетов, производится
 * их статистическая обработка с целью определения эффктивного значения
 * измеряемой величины и других характеристик (дисперсия, максимальное и
 * минимальное значение) на данном отрезке. Полученные значения используются для
 * сохранения и представления результатов измерения пользователю.<br>
 * Накопление данных производятся специальным
 * методом, вызываемым драйвером в при поступлении каждого ответного пакета в
 * рамках потока чтения данных от устройства. Этот метод должен завершить свою
 * работу до момента поступления нового пакета данных от устройства.<br>
 * Для выполнения статистической обработки и сохранения полученных 
 * результатов или для
 * вывода их на дисплей используется отдельный метод, выполняющийся в отдельныом
 * потоке. Время, необходсохранение для работы этого метода не должно превышать
 * время, необходимое для приема количества пакетов, подлежащих статистической
 * обработке за один раз.<br>
 * Так как накопление очередной порции результатов и обработка предыдущей выполняются
 * параллельно, то необходимо иметь не менее двух буферов для размещения
 * накапливаемых результатов и переключать процесс накоплени с одного на 
 * другой при каждом начале работы метода обработки результатов.<br>
 * Оба указанным метода являются абстрактными и подлежат реализации при
 * разработке драйверов конкретных устройств.<br>
 * Кроме этих абстрактных методов 
 * есть еще одни абстрактный метод, который вызывается при отсутствии ответных 
 * пакетов после отправки команды. Этот метод может использоваться для фиксации
 * факта потери связи с устройством.
 *
 * Класс реализует следующий функционал:
 * <ol>
 * <li> Создание объекта драйвера с помощью конструктора, принимающего в
 * качестве параметров адреса локального и удаленного сокета, через которые
 * ведется обмен данными, продолжительность серии в милисекундах и интервал
 * отправки повторяемых команд.
 * <li> Запуск постоянно функционирующих потоков передачи команд устройству,
 * приема даных от устройства и сохранения результатов статистической обработки.
 * <li> Прием от внешних программ разовых команд и команд подержания сессии с
 * последующей передачей их устройству с установленными временными интервалами.
 * <li> Прием от устройства серий пакетов и вызов обработчика принятых данных.
 * <li> Вызов метода обработка принятых пакетов.
 * <li> Вызов метода для сохранения полученных результатов.
 * <li> Реализация механизма блокировки внешней программы до момента получения
 * ответа на разовую команду от устройства.
 * <li> Выявление факта потери связи с устройством (отсутствие ответной серии на
 * переданную команду).
 * <li> Завершение работы и закрытие всех созданых сокетов.
 * </ol>
 * 
 * В классе реализованы следующие блокировки работы:<ol>
 * <li> Поток отправки команд и пакетов для поддержания работы устройства организован 
 * в виде цикла, завершаюшего работу при вызове метода {@code stop}, блокируется
 * секцией {@code sndLock} на время заданное интервалом отправки повторяемых команд. <br>
 * Эта секция сбрасывается при вызове метода {@code setCommand} и при остановки работы драйвера.
 * <li> Поток приема данный от устройства блокируется методом {@code DatagramSocket.receive}, на
 * время длительности ответной серии.
 * <li> Поток отправки данных внешней программе, организован в виде цикла, завершаюшего 
 * работу при вызове метода {@code stop}, блокируется секцией {@code pointLock}. <br>
 * Эта секция сбрасывается при вызове метода {@code newPointReady}, этот метод должен
 * быть вызван внешней программой после завершения накопления буфера АСкана.<br>
 * 
 * </ol>
  * @author MalginAS
 */
public abstract class GeneralUDPDevice {
    private static final Logger log = LoggerFactory.getLogger(GeneralUDPDevice.class);

    /**
     * Нет ошибок.
     */
    public static final int RC_OK = 0;
    /**
     * Возник SocketException при создании сокета.
     */
    public static final int RC_SOCKETEXCEPTION = 1;
    /**
     * Возник IOException при создании сокета.
     */
    public static final int RC_IOEXCEPTION = 2;
    /**
     * Неправильные параметры драйвера.
     */
    public static final int RC_ERRPARAMS = 3;
    /**
     * Статус разовой команды. Команда отправлена на устройство, ответа пока
     * нет.
     */
    public static final int CS_INPROGRESS = -1;
    /**
     * Статус разовой команды. Получен ответ от устройства на разовую команду.
     */
    public static final int CS_OK = 0;
    /**
     * Статус разовой команды. Истекло время ожидания ответа на команду.
     */
    public static final int CS_NORESPONCE = 1000;

    private final long serTime;       // Прдолжительность серии в милисекунтах
    private final long sendTime;      // Интервал отправки повторяемых команд.
    private DatagramPacket serCmd = null;       // Повторяемая команда.
    private DatagramPacket curCmd = null;       // Разовая команда.
    private final InetSocketAddress local;                // Локальный сокет
    private final InetSocketAddress remote;               // Удаленный сокет.
    private DatagramSocket sock = null;
    private final byte[] rcvBuf;                      // Буфер для приема данных
    private DatagramPacket rcvPack;             // Принятый пакет.

    private boolean isStarted = false;          // флаг цикла потоков драйвера.
    /**
     * Имя драйвера для добавления к именам потоков чтеня, отправки и
     * обработки данных.
     */
    public String driverName = "DefaultUDP"; // Имя драйвера

    private boolean cmdSended = false;          // Флаг наличия отправленной команды
    private boolean noResponce = false;         // Флаг отсутствия ответа от устройства.

    private int error;
    private String errMessage;

    private long sendedCmd = 0;                 // Количество отправленных разовых команд
    private long sendedSer = 0;                 // Количество отправленных повторяемых команд
    private long received = 0;                  // Количество принятых пакетов.

    private Thread dgSnd = null;                        // Поток отправки пакетов
    private Thread dgRcv = null;                        // Поток приема пакетов
    private Thread dgPoin = null;                       // Поток обработки новой точки результатов
    private final Object sndLock = new Object();        // Блокировка цикла отправки
    private final Object curCmdLock = new Object();     // Блокировка ожидания завершения
    // Разовой команды.
    private int currCmdStatus = CS_NORESPONCE;          // Статус текущей команды.
    private final Object pointLock = new Object();      // Блокировка ожидания новой точки.
    private GeneralUDPDeviceLissener fireEv = null;
    public final int sendSize;

    /**
     * Конструктор драйвера устройства.
     *
     * @param serTime длительность серии ответных пакетов в милисекундах.
     * @param sendTime интервал отправки повторяемых команд в милисекундах.
     * @param local локальный сокет.
     * @param remote удаленный сокет.
     * @param size размер пакета принимаемых, отправляемых данных
     */
    public GeneralUDPDevice(long serTime, long sendTime, InetSocketAddress local, InetSocketAddress remote, int size) {
        this.serTime = serTime;
        this.sendTime = sendTime;
        this.local = local;
        this.remote = remote;
        sendSize = size;
        rcvBuf = new byte[size];
    }

    /**
     * Сброс ошибочного состояния. Вызывается после успешной обработки ошибочной
     * ситуации.
     */
    public void resetError() {
        error = RC_OK;
        errMessage = null;
    }

    /**
     * Возвращает код последней ошибки. Действующие коды описаны в виде констант
     * с префиксом RC_ данного класса.
     *
     * @return код последней ошибки.
     */
    public int getError() {
        return error;
    }

    /**
     * Возвращает строку с описанием ошибки,
     *
     * @return Опсание последней ошибки или {@code  null} если ошибки нет.
     */
    public String getErrMessage() {
        return errMessage;
    }

    /**
     * Запуск драйвера.<br>
     * Создается сокет и запускаются потоки отправки и према пакетов.
     */
    public void start() {
        if(sock != null)return;
        resetError();
        if (local == null) {
            error = RC_ERRPARAMS;
            errMessage = "Не задан адрес локального сокета";
            sock = null;
            return;
        }
        if (remote == null) {
            error = RC_ERRPARAMS;
            errMessage = "Не задан адрес удаленного сокета";
            sock = null;
            return;
        }
        if (serTime == 0) {
            error = RC_ERRPARAMS;
            errMessage = "Не задано время серии";
            sock = null;
            return;
        }
        if (sendTime == 0) {
            error = RC_ERRPARAMS;
            errMessage = "Не задан интервал отправки повторяемых команд";
            sock = null;
            return;
        }
        if (rcvBuf.length == 0) {
            error = RC_ERRPARAMS;
            errMessage = "Не задан размер принимаемых пакетов";
            sock = null;
            return;
        }
        try {
            sock = new DatagramSocket(local);
        } catch (SocketException ex) {
            error = RC_SOCKETEXCEPTION;
            errMessage = "SocketException при создании: " + ex.getLocalizedMessage();
            sock = null;
            return;
        }
        try {
            sock.setSoTimeout((int) serTime);
        } catch (SocketException ex) {
            error = RC_SOCKETEXCEPTION;
            errMessage = "SocketException при установке Timeout: " + ex.getLocalizedMessage();
            sock = null;
            return;
        }
        rcvPack = new DatagramPacket(rcvBuf, rcvBuf.length);
        // Поток отправки команд.
        isStarted = true;
        sendedCmd = 0;
        sendedSer = 0;
        received = 0;
        dgSnd = new Thread(() -> {
            sender();
        }, "ThreadUDPSender " + driverName);
        dgSnd.start();
        // Поток приема команд.
        dgRcv = new Thread(() -> {
            receiver();
        }, "ThreadUDPReceiver " + driverName);
        ThreadGroup tg = dgRcv.getThreadGroup();
        dgRcv.setPriority(tg.getMaxPriority());
        dgRcv.start();
        dgPoin = new Thread(() -> {
            pointProcessor();
        }, "TheadPoinProc " + driverName);
        dgPoin.start();
    }

    /**
     * Остановка драйвера и закрытие сокета.
     */
    public void stop() {
        resetError();
        isStarted = false;
        synchronized (sndLock) {
            sndLock.notifyAll();
        }
        synchronized (pointLock) {
            pointLock.notifyAll();
        }
        if (dgRcv != null) {
            try {
                dgRcv.join();
            } catch (InterruptedException ex) {
            }
        }
        if (dgSnd != null) {
            try {
                dgSnd.join();
            } catch (InterruptedException ex) {
            }
        }
        if (dgPoin != null) {
            try {
                dgPoin.join();
            } catch (InterruptedException ex) {
            }
        }
        if (sock != null) {
            sock.close();
        }
        sock = null;
    }

    /**
     * Возвращает количество отправленных разовых команд.
     *
     * @return количество отправленных разовых команд с момента старта драйвера.
     */
    public long getSendedCmd() {
        return sendedCmd;
    }

    /**
     * Возвращает количество отправленных повторяемых команд.
     *
     * @return количество отправленных повторяемых команд с момента старта
     * драйвера.
     */
    public long getSendedSer() {
        return sendedSer;
    }

    /**
     * Возвращает количество принятых пакетов данных
     *
     * @return количество пакетов даннх принятых с момента запуска драйвера.
     */
    public long getReceived() {
        return received;
    }

    /**
     * Задает разовую команду и инициирует процесс ее отправки.
     * @param buf буфер данных команд.
     */
    public void setCommand(byte[] buf) {
        synchronized (sndLock) {
            curCmd = new DatagramPacket(buf, buf.length, remote);
            setCurCmdStatus(CS_INPROGRESS);
            sndLock.notifyAll();
        }
    }

    /**
     * Задает повторяемую команду и инициирует процесс ее отправки.
     *
     * @param buf буфер данных команды. Если {@code null}, то повторяемая
     * команда отменяется.
     */
    public void setSerCommand(byte[] buf) {
        synchronized (sndLock) {
            if (buf == null) {
                serCmd = null;
                return;
            }
            serCmd = new DatagramPacket(buf, buf.length, remote);
            sndLock.notifyAll();
        }
    }
    /**
     * Устанавливает факт наличия серийной команды.
     * @return {@code true} если верийная команда есть,{@code false} -
     * в противнм случае.
     */
    public boolean isSerCmdOn(){
        return serCmd != null;
    }

    /**
     * Возвращает адрес локального сокета.
     *
     * @return адрес локального сокета.
     */
    public InetSocketAddress getLocal() {
        return local;
    }

    /**
     * Возвращает адрес удаленного сокета.
     *
     * @return адрес удаленного сокета.
     */
    public InetSocketAddress getRemote() {
        return remote;
    }

    /**
     * Метод блокирует поток до получения ответа на разовую команду.<br>
     * @return Код завершения ожидания ответа. Одна из констант данного класса
     * с префиксом CS_.
     */
    public int waitForCommandResponce() {
        long endTime = System.currentTimeMillis() + serTime * 2;
        synchronized (curCmdLock) {
            while (System.currentTimeMillis() < endTime) {
                if (currCmdStatus != CS_INPROGRESS) {
                    return currCmdStatus;
                }
                try {
                    curCmdLock.wait(serTime);
                } catch (InterruptedException ex) {
                }
            }
        }
        currCmdStatus = CS_NORESPONCE;
        log.warn("Reponse wating timeout has been expired for [{}]", driverName);
        return currCmdStatus;
    }

    /**
     * Обработчик поступившего пакета.<br>
     * Этот метод вызывается при поступлении нового пакета от устройства. Он
     * должен реализовать разбор поступившего пакета, накопление результатов
     * для их последующей статистической обработки.
     *
     * @param buf буфер принятых данных.
     */
    public abstract void newDataAvailable(byte [] buf);

    /**
     * Метод, вызываемый при потери связи с устройством.<br>
     * Факт потери связи фиксируется, если с момента отправки последней команды
     * устройству истекло время серии, но ни один ответный пакет не пришел.
     */
    public abstract void noResponce();

    /**
     * Метод обработки данных для очередной точки результатов.<br>
     * Этот метод вызывается в отдельном потоке после завершения статистической
     * обрвботки результатов измерения. Он может использоваться для записи
     * результатов в базу данных, либо для их отображения в реальном масштабе
     * времени на дисплее. Время работы метода не должно превышать время
     * поступления пакетов для одного сеанса статистической обработки.
     */
    public abstract void newPointAvailable();

    /**
     * Метод сигнализирует о завершении статистической обработки результатов для
     * одной точки и инициирует вызов метода {@code newPointAvailable()} в
     * отдельном потоке. Этим методом нужно пользоваться в обработчике пакетов
     * {@code newDataAvailable()} после завершения статистической обработки
     * результатов.
     */
    protected void newPointReady() {
        synchronized (pointLock) {
            pointLock.notifyAll();
        }
    }

    /**
     * Устанавливает новое значение статуса разовой команды и уведомляет об этом
     * всех ожидаюзщих.<br>
     * Этим методом должен воспользоваться обработчик поступивших данных при
     * обнаружении ответа на разовую команду в поступившем пакете. В этом случеа
     * устанавливается статус {@code CS_OK}.
     *
     * @param stat новый статус разовой команды. Одна из констант данного класса
     * с префиксом CS_.
     */
    protected void setCurCmdStatus(int stat) {
        synchronized (curCmdLock) {
            currCmdStatus = stat;
            curCmdLock.notifyAll();
        }
    }

    /**
     * Поток отправки пакетов
     */
    private void sender() {
        DatagramPacket pts;
        while (isStarted) {
            synchronized (sndLock) {
                pts = curCmd;
                if (pts == null) {
                    pts = serCmd;
                }
                if (pts != null) {
                    try {
                        sock.send(pts);
                        noResponce = false;
                        cmdSended = true;
                    } catch (IOException ex) {
                        error = RC_IOEXCEPTION;
                        errMessage = "IOException при отправке пакета: " + ex.getLocalizedMessage();
                    }
                    if (pts == curCmd) {
                        curCmd = null;
                        sendedCmd++;
                    } else {
                        sendedSer++;
                    }
                }
                try {
                    sndLock.wait(sendTime);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    /**
     * Поток приема пакетов
     */
    private void receiver() {
        while (isStarted) {
            try {
                sock.receive(rcvPack);
            } catch (IOException ex) {
                if (SocketTimeoutException.class.isInstance(ex)) {
                    if (cmdSended) {
                        if (noResponce) {
                            // это второй заход с момента отпавки команды,
                            // значит связи точно нет.
                            noResponce();
                            noResponce = false;
                            cmdSended = false;
                        } else {
                            // Это первый заход после отправки команды,
                            // команда была отправлена в течение предыдущего времени
                            // серии и еще пока не истекло полное время. Ставим флаг и 
                            // продолжаем ждать.
                            noResponce = true;
                        }
                    }
                    continue;
                }
                error = RC_IOEXCEPTION;//
                log.error("Error accepting package.", ex);
            }
            
            if (rcvPack.getLength() == rcvBuf.length) {
                // Хороший пакет. обрабатываем.
                noResponce = false;
                cmdSended = false;
                received++;
                newDataAvailable(rcvBuf);
            }
        }
    }
    /**
     * Добавляем слушателя окончания обработки новых данных
     * по очередной точке сканирования. 
     * @param ev слушатель, {@code  null} для удаление слушателя.
     */
    public void addListenerNewPoint(GeneralUDPDeviceLissener ev){
        fireEv = ev;
    }
    /**
     * Поток обработки точек результатов.
     */
    private void pointProcessor() {
        synchronized (pointLock) {
            while (isStarted) {
                try {
                    pointLock.wait();
                } catch (InterruptedException ex) {
                }
                newPointAvailable();
                if(fireEv != null){
                    fireEv.onEvent();
                }
            }
        }
    }
    /**
//Для проверки изменения параметров при переключении в полный сигнал
savePrmToFile(activChanParam);
    //Для проверки изменения параметров при переключении в полный сигнал
    * Выводит 
     * @param activChanParam 
     */
    private void savePrmToFile(byte[] buf, String nm) {
        try {
            DataOutputStream fl = new DataOutputStream(new FileOutputStream(nm));
            String str;
            fl.writeBytes(String.format("%1$td.%1$tm.%1$ty %1$tH:%1$tM:%1$tS данные с 58 байта, по 100 байт \n",new Date()));
            for (int i = 0; i < buf.length - 10; i += 10) {
                str = String.format("%02x %02x %02x %02x %02x %02x %02x %02x %02x %02x \n",
                        buf[i], buf[i + 1], buf[i + 2], buf[i + 3], buf[i + 4], buf[i + 5], buf[i + 6],
                        buf[i + 7], buf[i + 8], buf[i + 9]);
                fl.writeBytes(str);
            }
            fl.writeBytes("\n\n");
            fl.close();
        } catch (IOException ex) {
            log.error("Error saving parameters to file [{}]", nm, ex);
        }
    }
}
