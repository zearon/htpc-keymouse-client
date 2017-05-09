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
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(1, null);
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void stopBrowser() {
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(2, null);
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void sendTextInput(String text) {
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(3, dao -> {
                    dao.write(text.getBytes("utf-8"));
                });
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void sendKeyPress(String keyText) {
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(4, dao -> {
                    dao.write(keyText.getBytes("utf-8"));
                });
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void sendMouseMove(int deltaX, int deltaY) {
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(5, dao -> {
                    dao.writeInt(deltaX);
                    dao.writeInt(deltaY);
                });
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void sendMouseClick(MouseButton mouseButton) {
        instructionQueue.add(() -> {
            int button = mouseButton.ordinal() + 1;
            try {
                sendPacketToServer(6, dao -> {
                    dao.writeInt(button);
                });
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void sendMouseDoubleClick(MouseButton mouseButton) {
        instructionQueue.add(() -> {
            int button = mouseButton.ordinal() + 1;
            try {
                sendPacketToServer(7, dao -> {
                    dao.writeInt(button);
                });
            } catch (IOException e) {
                Log.e("server_controller", "Cannot send packet to server", e);
                log("Cannot send packet to server", e);
            }
        });
        synchronized (instructionQueue) {
            instructionQueue.notify();
        }
    }

    public void sendOtherCommand(int instCode, String payload) {
        instructionQueue.add(() -> {
            try {
                sendPacketToServer(instCode, dao -> {
                    dao.write(payload.getBytes("utf-8"));
                });
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
