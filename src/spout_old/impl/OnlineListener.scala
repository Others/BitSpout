package spout_old.impl

import java.net.ServerSocket
import java.net.Socket
import java.net.InetAddress

import spout_old.inject.Constants
import spout_old.inject.RemoteListener


class OnlineListener extends RemoteListener[InetAddress]{
    val socket=new ServerSocket(Constants.SERVER_PORT)
    @volatile var connectedSockets:List[Socket]=List.empty

    new Thread(new Runnable(){
        def run()={
            while(true){
                val newSocket=socket.accept()
                connectedSockets=connectedSockets :+ newSocket
            }
        }
    }).start()
    
    def getMessage():(InetAddress, Seq[Byte], Seq[InetAddress] => _) = {
        while(true){
            val currentSockets=connectedSockets
            for(socket <- currentSockets){
                if(socket.isConnected()){
                    val inputStream=socket.getInputStream()
                    if(inputStream.available() != 0){
                        val buffer=new Array[Byte](2000)
                        val readBytes=inputStream.read(buffer)
                        return (socket.getInetAddress(), buffer.toSeq.take(readBytes), (seq:Seq[InetAddress]) =>{
                            socket.getOutputStream().write(seq.flatMap(_.getAddress()).toArray)
                        })
                    }
                }
            }
            Thread.sleep(10000)
        }
        throw new IllegalStateException()
    }
}