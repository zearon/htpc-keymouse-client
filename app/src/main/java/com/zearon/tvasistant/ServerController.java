package com.zearon.tvasistant;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
    private DatagramSocket socket;
    private Thread networkSendThread;
    private Thread networkRecvThread;
    private ConcurrentLinkedQueue<Instruction> instructionQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<OnResponseListener> responseListeners = new ConcurrentLinkedQueue<>();

    public ServerController() {
        config = Config.getInstance();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        networkSendThread = new Thread(() -> networkSendThreadMain(), "Network-send");
        networkSendThread.setDaemon(true);
        networkSendThread.start();

        networkRecvThread = new Thread(() -> networkRecvThreadMain(), "Network-recv");
        networkRecvThread.setDaemon(true);
        networkRecvThread.start();
    }

    public enum MouseButton {
        LEFT, MIDDLE, RIGHT, WHEEL_UP, WHEEL_DOWN
    }

    interface Instruction {
        void sendInstruction(DatagramSocket socket, ByteArrayOutputStream baos, DataOutputStream dos);
    }

    interface AssemblePacketCallback {
        void assemblePayload(DataOutputStream dos) throws IOException;
    }

    public interface OnResponseListener {
        void onResponse(String message);
    }

    private void networkSendThreadMain() {
        // Construct udp package in byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        while (true) {
            while (!instructionQueue.isEmpty()) {
                Instruction instruction = instructionQueue.poll();
                instruction.sendInstruction(socket, baos, dos);
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

    private void networkRecvThreadMain() {
        // Create buffer to hold received messages.
        byte[] recvBuffer = new byte[2014];
        int packetDataLength;
        DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);

        while (true) {
            try {
                socket.receive(packet);
                String responseMsg = new String(recvBuffer, packet.getOffset(), packet.getLength(), "utf-8");

                // call all OnResponseListeners
                for (OnResponseListener listener : responseListeners) {
                    listener.onResponse(responseMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCommand(int instCode, AssemblePacketCallback callback) {
        instructionQueue.add((socket, baos, dos) -> {
            try {
                sendPacketToServer(socket, baos, dos, instCode, callback);
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    private void sendPacketToServer(DatagramSocket socket, ByteArrayOutputStream baos, DataOutputStream dos,
                                    int instCode, AssemblePacketCallback callback) throws IOException {
        // Reset output buffer
        baos.reset();

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

        InetAddress addr = InetAddress.getByName(config.getServerHostname());
        DatagramPacket packet = new DatagramPacket(data, dataLength, addr, config.getServerPort());
        socket.send(packet);
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

    public void addOnResponseListener(OnResponseListener listener) {
        responseListeners.add(listener);
    }

    public void startBrowser() {
        addCommand(0x01, null);
    }

    public void stopBrowser() {
        addCommand(0x02, null);
    }

    public void changeSoundOutputDevice() {
        int soundCard = Config.getInstance().getSoundCardIndex();
        String deviceName = Config.getInstance().getOutputSoundDeviceName();
        addCommand(0x0a, dao -> {
            dao.writeInt(soundCard);
            dao.write(deviceName.getBytes("utf-8"));
        });
    }

    public void sendTextInput(String text) {
        addCommand(0x03, dao -> {
            dao.write(text.getBytes("utf-8"));
        });
    }

    public void sendKeyPress(String keyText) {
        addCommand(0x04, dao -> {
            dao.write(keyText.getBytes("utf-8"));
        });
    }

    /**
     * Send key event
     * @param keyText key text
     * @param eventType eventType. 1 - key_down, 2 - key_up
     */
    public void sendKeyEvent(String keyText, int eventType) {
        addCommand(0x09, dao -> {
            dao.writeInt(eventType);
            dao.write(keyText.getBytes("utf-8"));
        });
    }

    public void sendMouseMove(int deltaX, int deltaY) {
        addCommand(0x05, dao -> {
            dao.writeInt(deltaX);
            dao.writeInt(deltaY);
        });
    }

    public void sendMouseClick(MouseButton mouseButton) {
        int button = mouseButton.ordinal() + 1;
        addCommand(0x06, dao -> {
            dao.writeInt(button);
        });
    }

    public void sendMouseDoubleClick(MouseButton mouseButton) {
        int button = mouseButton.ordinal() + 1;
        addCommand(0x07, dao -> {
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
        addCommand(0x08, dao -> {
            dao.writeInt(button);
            dao.writeInt(eventType);
        });
    }

    public void sendOtherCommand(int instCode, String payload) {
        addCommand(instCode, dao -> {
            dao.write(payload.getBytes("utf-8"));
        });
    }

    public void log(String msg, Throwable e) {

    }

}
