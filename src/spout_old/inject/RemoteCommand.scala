package spout_old.inject

import java.io.{DataInputStream, DataOutputStream}
import java.net.{InetAddress, InetSocketAddress, Socket}
import java.nio.ByteBuffer

import scala.util.Random

trait RemoteCommand[T]{
    def setupServer(server:String, port:Char, fileHashType:Byte, fileHash:Seq[Byte]):ServerConnection[T]
    
    def pollPeers():(Seq[Byte], T)
    
    def transmitToPeer(address:T, message:Seq[Byte])
}

trait ServerConnection[T]{
    def getPeers():Seq[T]
    
    def endConnection()
    
    def feedWatchdog()
}