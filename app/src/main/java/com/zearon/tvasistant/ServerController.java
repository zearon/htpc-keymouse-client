package com.zearon.tvasistant;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zhiyuangong on 17/5/9.
 */
public class ServerController {
    private static ServerController instance;

    public static synchronized ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    private Config config;
    private Thread networkThread;
    private ConcurrentLinkedQueue<Instruction> instructionQueue = new ConcurrentLinkedQueue<>();

    public ServerController() {
        config = Config.getInstance();

        networkThread = new Thread(() -> networkThreadMain());
        networkThread.setDaemon(true);
        networkThread.start();
    }

    public enum MouseButton {
        LEFT, MIDDLE, RIGHT, WHEEL_UP, WHEEL_DOWN
    }

    private static interface AssemblePacketCallback {
        public void assemblePayload(DataOutputStream dos) throws IOException;
    }

    private void networkThreadMain() {
        while (true) {
            while (!instructionQueue.isEmpty()) {
                Instruction instruction = instructionQueue.poll();
                instruction.sendInstruction();
            }

            try {
                synchronized (instructionQueue) {
                    instructionQueue.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("server_controller", "Cannot wait on instruction queue.", e);
            }
        }
    }

    private void sendPacketToServer(int instCode, AssemblePacketCallback callback)
            throws IOException {

        // Construct udp package in byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write instruction code
        dos.writeByte(instCode);
        // Write authentication info
        setAuthentication(dos);
        // Write payload
        if (callback != null)
            callback.assemblePayload(dos);

        byte[] data = baos.toByteArray();
        int dataLength = data.length;
        Log.v("server_controller", "***Packet: " + Arrays.toString(data));

        InetAddress addr = InetAddress.getByName(config.getServerHost());
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(data, dataLength, addr, config.getServerPort());
        socket.send(packet);

        dos.close();
    }

    /**
     * Set authentication info in the packet.
     * @param dos DataOutputStream
     */
    private void setAuthentication(DataOutputStream dos) throws IOException {
        // Not implemented yet.
        int bytes = 0;

        // write start index of payload of the packet
        dos.writeByte(bytes + 2);
    }

    public void startBrowser() {
        addCommand(1, null);
    }

    public void stopBrowser() {
        addCommand(2, null);
    }

    public void sendTextInput(String text) {
        addCommand(3, dao -> {
            dao.write(text.getBytes("utf-8"));
        });
    }

    public void sendKeyPress(String keyText) {
        addCommand(4, dao -> {
            dao.write(keyText.getBytes("utf-8"));
        });
    }

    /**
     * Send key event
     * @param keyText key text
     * @param eventType eventType. 1 - key_down, 2 - key_up
     */
    public void sendKeyEvent(String keyText, int eventType) {
        addCommand(9, dao -> {
            dao.writeInt(eventType);
            dao.write(keyText.getBytes("utf-8"));
        });
    }

    public void sendMouseMove(int deltaX, int deltaY) {
        addCommand(5, dao -> {
            dao.writeInt(deltaX);
            dao.writeInt(deltaY);
        });
    }

    public void sendMouseClick(MouseButton mouseButton) {
        int button = mouseButton.ordinal() + 1;
        addCommand(6, dao -> {
            dao.writeInt(button);
        });
    }

    public void sendMouseDoubleClick(MouseButton mouseButton) {
        int button = mouseButton.ordinal() + 1;
        addCommand(7, dao -> {
            dao.writeInt(button);
        });
    }

    /**
     * Send mouse event
     * @param mouseButton mouse button
     * @param eventType eventType. 1 - mouse_down, 2 - mouse_up
     */
    public void sendMouseEvent(MouseButton mouseButton, int eventType) {
        int button = mouseButton.ordinal() + 1;
        addCommand(8, dao -> {
            dao.writeInt(button);
            dao.writeInt(eventType);
        });
    }

    public void sendOtherCommand(int instCode, String payload) {
        addCommand(instCode, dao -> {
            dao.write(payload.getBytes("utf-8"));
        });
    }

    private void addCommand(int instCode, AssemblePacketCallback callback) {
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(instCode, callback);
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void log(String msg, Throwable e) {

    }

}

interface Instruction {
    void sendInstruction();
}
