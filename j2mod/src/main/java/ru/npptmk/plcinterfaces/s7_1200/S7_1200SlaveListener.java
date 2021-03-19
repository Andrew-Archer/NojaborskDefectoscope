/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.plcinterfaces.s7_1200;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.ModbusListener;
import com.ghgande.j2mod.modbus.net.TCPSlaveConnection;
import com.ghgande.j2mod.modbus.util.ThreadPool;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Приемник сообщений от контроллера по протоколу Modbus TCP.
 *
 * @author MalginAS
 */
public class S7_1200SlaveListener implements ModbusListener {

    private ServerSocket m_ServerSocket = null;
    private ThreadPool m_ThreadPool;
    private Thread m_Listener;
    private int m_Port = Modbus.DEFAULT_PORT;
    private int m_Unit = 0;
    private int m_FloodProtection = 5;
    private boolean m_Listening;
    private InetAddress m_Address;
    private I_PLCDataUpdated[] upd;

    /**
     * Sets the port to be listened to.
     *
     * @param port the number of the IP port as <tt>int</tt>.
     */
    public void setPort(int port) {
        m_Port = port;
    }

    /**
     * Gets the unit number supported by this Modbus/TCP connection. A
     * Modbus/TCP connection, by default, supports unit 0, but may also support
     * a fixed unit number, or a range of unit numbers if the device is a
     * Modbus/TCP gateway. If the unit number is non-zero, all packets for any
     * other unit number should be discarded.
     *
     * @return
     * @returns unit number supported by this interface.
     */
    @Override
    public int getUnit() {
        return m_Unit;
    }

    /**
     * Sets the unit number to be listened for. A Modbus/TCP connection, by
     * default, supports unit 0, but may also support a fixed unit number, or a
     * range of unit numbers if the device is a Modbus/TCP gateway.
     *
     * @param unit the number of the Modbus unit as <tt>int</tt>.
     */
    @Override
    public void setUnit(int unit) {
        m_Unit = unit;
    }

    /**
     * Sets the address of the interface to be listened to.
     *
     * @param addr an <tt>InetAddress</tt> instance.
     */
    public void setAddress(InetAddress addr) {
        m_Address = addr;
    }

    /**
     * Starts this <tt>ModbusTCPListener</tt>.
     *
     * @deprecated
     */
    public void start() {
        m_Listening = true;

        m_Listener = new Thread(this);
        m_Listener.start();
    }

    /**
     * Stops this <tt>ModbusTCPListener</tt>.
     */
    @Override
    public void stop() {
        m_Listening = false;
        try {
            m_ServerSocket.close();
            m_Listener.join();
        } catch (IOException | InterruptedException ex) {
        }
    }

    /**
     * Accepts incoming connections and handles then with
     * <tt>TCPConnectionHandler</tt> instances.
     */
    @Override
    public void run() {
        try {
            /*
			 * A server socket is opened with a connectivity queue of a size
			 * specified in int floodProtection. Concurrent login handling under
			 * normal circumstances should be allright, denial of service
			 * attacks via massive parallel program logins can probably be
			 * prevented.
             */
            m_ServerSocket = new ServerSocket(m_Port, m_FloodProtection,
                    m_Address);
            /*
			 * Infinite loop, taking care of resources in case of a lot of
			 * parallel logins
             */

            m_Listening = true;
            while (m_Listening) {
                Socket incoming = m_ServerSocket.accept();
                if (m_Listening) {
                    m_ThreadPool.execute(new S7_1200ConnectionHandler(
                            new TCPSlaveConnection(incoming),upd));
                } else {
                    incoming.close();
                }
            }
        } catch (SocketException iex) {
        } catch (IOException e) {
        }
    }

    /**
     * Set the listening state of this <tt>ModbusTCPListener</tt> object. A
     * <tt>ModbusTCPListener</tt> will silently drop any requests if the
     * listening state is set to <tt>false</tt>.
     *
     * @param b
     */
    @Override
    public void setListening(boolean b) {
        m_Listening = b;
    }

    /**
     * Tests if this <tt>ModbusTCPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @return true if listening (and accepting incoming connections), false
     * otherwise.
     */
    @Override
    public boolean isListening() {
        return m_Listening;
    }

    /**
     * Start the listener thread for this serial interface.
     *
     * @return
     */
    @Override
    public Thread listen() {
        m_Listening = true;
        Thread result = new Thread(this);
        result.start();

        return result;
    }

    /**
     * Constructs a ModbusTCPListener instance.<br>
     *
     * @param poolsize the size of the <tt>ThreadPool</tt> used to handle
     * incoming requests.
     * @param addr the interface to use for listening.
     * @param upd
     */
    public S7_1200SlaveListener(int poolsize, InetAddress addr, I_PLCDataUpdated[] upd) {
        m_ThreadPool = new ThreadPool(poolsize);
        m_Address = addr;
        this.upd = upd;
    }

    /**
     * /**
     * Constructs a ModbusTCPListener instance. This interface is created to
     * listen on the wildcard address, which will accept TCP packets on all
     * available interfaces.
     *
     * @param poolsize the size of the <tt>ThreadPool</tt> used to handle
     * incoming requests.
     * @param upd
     */
    public S7_1200SlaveListener(int poolsize, I_PLCDataUpdated[] upd) {
        m_ThreadPool = new ThreadPool(poolsize);
        this.upd = upd;
        try {
            /*
			 * TODO -- Check for an IPv6 interface and listen on that
			 * interface if it exists.
             */
            m_Address = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        } catch (UnknownHostException ex) {
            // Can't happen -- size is fixed.
        }
    }
}
