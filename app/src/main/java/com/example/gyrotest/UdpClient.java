package com.example.gyrotest;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UdpClient{
    private DatagramPacket datagramPacket;
    private InetAddress address;
    private byte[] data;

    public UdpClient(DatagramPacket datagramPacket, InetAddress address) {
        this.datagramPacket = datagramPacket;
        this.address = address;
    }

}
