package spout_old.inject

import java.nio.file.Path

trait PacketManager{
    def getStartSignal(fileHashType:Byte, fileHash:Seq[Byte]):Array[Byte]
    def getStopSignal(fileHashType:Byte, fileHash:Seq[Byte]):Array[Byte]
    
    def getPacketHandler(fileHashType:Byte, fileHash:Seq[Byte], file:Path):PacketHandler
    def getPacketHandler(fileSize:Long, fileBlocks:Long, fileHashType:Byte, fileHash:Seq[Byte]):PacketHandler
}

trait PacketHandler{
    def addPacket(packet:Seq[Byte])
   
    //Decryption
    def tryDecrypt():Option[Seq[Byte]]
    
    def canDecrypt():Boolean
    
    def getPacket():Option[Seq[Byte]]
}