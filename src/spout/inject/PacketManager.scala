package spout.inject

import java.nio.file.Path

trait PacketManager{
    def getPacketHandler(fileHashType:Byte, fileHash:Seq[Byte], file:Path):PacketHandler
    def getPacketHandler(fileHashType:Byte, fileHash:Seq[Byte]):PacketHandler
    def getPacketHandler(fileSize:Long, fileBlocks:Long, fileHashType:Byte, fileHash:Seq[Byte]):PacketHandler
}

trait PacketHandler{
    def addPacket(packet:Seq[Byte])
    
    //Decryption
    def tryDecrypt():Option[Seq[Byte]]
    
    def canDecrypt():Boolean
    
    def getPacket():Option[Seq[Byte]]
    
    //Signals
    def generateStartSignal():Seq[Byte]
    
    def generateStopSignal():Seq[Byte]
    
    def generateWatchdogSignal():Seq[Byte]
    
    //Data signal
    def generateDataSignal():Option[Seq[Byte]]
}

