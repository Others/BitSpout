package spout_old.impl

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

import spout_old.inject.Constants
import spout_old.inject.RemoteCommand
import spout_old.inject.ServerConnection

class OnlineCommand extends RemoteCommand[InetAddress]{
    val datagramSocket=new DatagramSocket(Constants.CLIENT_PORT)
    val datagramBuffer=new DatagramPacket(new Array[Byte](5000), 5000)
    
    def pollPeers(): (Seq[Byte], InetAddress) = {
        datagramSocket.receive(datagramBuffer)
        val source=datagramBuffer.getAddress()
        val size=datagramBuffer.getLength()
        val data=datagramBuffer.getData()
        return (data.toSeq.splitAt(size)._1, source)
    }
    
    def transmitToPeer(address: InetAddress, message: Seq[Byte]): Unit = {
        val datagramMissive=new DatagramPacket(message.toArray, 0, message.size, address, Constants.CLIENT_PORT)
        datagramSocket.send(datagramMissive)
    }
    
    def setupServer(server: String, port: Char, fileHashType: Byte,fileHash: Seq[Byte]): ServerConnection[InetAddress] = {
        new OnlineServerConnection(server, port, fileHashType, fileHash)
    }
    
    class OnlineServerConnection(server: String, port: Char, fileHashType: Byte, fileHash: Seq[Byte]) extends ServerConnection[InetAddress]{
        val serverSocket=new Socket(server, port)        

        def endConnection(): Unit = {
            serverSocket.close();
        }
        
        def feedWatchdog(): Unit = {
            val signal:Seq[Byte]=Constants.KEEP_ALIVE +: fileHashType +: fileHash
            serverSocket.getOutputStream().write(signal.toArray)
        }
        
        def getPeers(): Seq[java.net.InetAddress] = {
            val byteBuffer:Array[Byte]=new Array[Byte](5000)
            val readBytes=serverSocket.getInputStream().read(byteBuffer)
            if(!(readBytes % 4 == 0)){
                throw new IllegalStateException()
            }
            var bytes=byteBuffer.toSeq.take(readBytes)
            var collectedIps:Seq[InetAddress]=Seq.empty
            while(bytes.size > 0){
                val (ip, newBytes) = bytes.splitAt(4)
                collectedIps=collectedIps :+ InetAddress.getByAddress(ip.toArray)
                bytes=newBytes
            }
            return collectedIps
        }
    }
}