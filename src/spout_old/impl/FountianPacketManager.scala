package spout_old.impl

import java.nio.ByteBuffer
import java.nio.file.Path

import scala.util.Try

import spout_old.inject.PacketHandler
import spout_old.inject.PacketManager
import spout_old.inject.Constants

class FountianPacketManager extends PacketManager{
    
    def getStartSignal(fileHashType: Byte,fileHash: Seq[Byte]): Array[Byte] = (Constants.SEND_DATA +: fileHashType +: fileHash).toArray
    
    def getStopSignal(fileHashType: Byte,fileHash: Seq[Byte]): Array[Byte] = (Constants.STOP_DATA +: fileHashType +: fileHash).toArray
    
    def getPacketHandler(fileSize: Long, fileBlocks: Long, fileHashType: Byte, fileHash: Seq[Byte]): PacketHandler = {
        new FountainHandler(fileSize)
    }
    
    def getPacketHandler(fileHashType: Byte, fileHash: Seq[Byte], file:Path): PacketHandler = ???  
}

class FountainHandler(fileSize: Long) extends PacketHandler{
    
    def addPacket(packet: Seq[Byte]): Unit = ???
    
    def canDecrypt(): Boolean = ???
        
    def getPacket(): Option[Seq[Byte]] = ???
    
    def tryDecrypt(): Option[Seq[Byte]] = ???
}

class FountainMessage(val numBlocks:Int, val paddingBytes:Int, private var choosenBlocks:Set[Int], private var content:Packet){
    protected val shortNumblocks=FountainMessage.toShort(numBlocks)
    protected val shortPaddingBytes=FountainMessage.toShort(paddingBytes)
    
    def blocks=choosenBlocks
    
    def message=content
    
    def update(block:Int, o:Packet)={
        if(choosenBlocks.contains(block)){
            choosenBlocks = choosenBlocks - block
            content = content.mergeInt(o, _ ^ _)
        }
    }
}

object FountainMessage{
    def toShort(value:Int):Short={
        if(value > 0xFFFF) throw new IllegalArgumentException("Int is too big!")
        return value.toShort
    }
    
    def fromShort(value:Short):Int={
        return value & 0xFFFF
    }
    
    implicit def toPacket(message:FountainMessage)={
        val buffer=ByteBuffer.allocate(2 + 2 + 2 + message.choosenBlocks.size * 2)
        buffer.putShort(message.shortNumblocks)
        buffer.putShort(message.shortPaddingBytes)
        buffer.putShort(FountainMessage.toShort(message.choosenBlocks.size))
        for(blockIndex <- message.choosenBlocks) buffer.putShort(FountainMessage.toShort(blockIndex))
        new Packet(buffer.array().toList ++ message.content)

    }
    
    def apply(packet:Packet):Try[FountainMessage]={
        Try{
            val buffer:ByteBuffer=ByteBuffer.wrap(packet.bytes.toArray)
            val numBlocks=fromShort(buffer.getShort())
            val paddingBytes=fromShort(buffer.getShort())
            val numSelectedBlocks=fromShort(buffer.getShort())
            val choosenBlocks=Range(0, numSelectedBlocks).map(_ => fromShort(buffer.getShort)).toSet
            val content=new Packet(Range(0, buffer.remaining()).map(_ => buffer.get()).toList)
            new FountainMessage(numBlocks, paddingBytes, choosenBlocks, content)
        }
    }
}