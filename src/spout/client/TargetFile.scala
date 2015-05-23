package spout.client

import scala.collection.mutable
import spout.inject.Constants._
import spout.inject.{RemoteCommand, PacketHandler, PacketManager}



class TargetFile[T](val fileSize:Long, val serverName:String, val fileHashType:Byte, val fileHash:Seq[Byte], remote:RemoteCommand[T], packet:Either[PacketManager, PacketHandler]){
    val fileBlocks=fileSize/512 + (if(fileSize % 512 > 0) 1 else 0)

    val peers:mutable.Set[T]=mutable.Set()
    val activePeers:mutable.Set[T]=mutable.Set()
    val targetPeers:mutable.Set[T]=mutable.Set()
    
    val server=remote.setupServer(serverName, SERVER_PORT, fileHashType, fileHash)
    val packetHandler=packet.fold(_.getPacketHandler(fileSize, fileBlocks, fileHashType, fileHash), x => x)
}