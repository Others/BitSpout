package spout_old.client.thread

import scala.collection.mutable
import scala.util.Random
import spout_old.client.TargetFile
import spout_old.inject.{RemoteCommand, PacketManager}

class TransmitterThread[T](files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T], packetGen:PacketManager) extends Runnable{
    def run()={
        while(true){
            val packets=files.values.map(file => (file, file.packetHandler.getPacket()))
            val filteredFiles=packets.filter{case(file, packet) => file.targetPeers.size > 0  && packet.isDefined}
            if(filteredFiles.isEmpty){
                Thread.sleep(1000)
            }else{
                for((file, packet) <- filteredFiles){
                   controller.transmitToPeer(file.targetPeers.toVector(Random.nextInt(file.targetPeers.size)), packet.get)
                }
            }
        }
    }
}