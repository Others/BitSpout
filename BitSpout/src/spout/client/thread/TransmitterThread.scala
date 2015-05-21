package spout.client.thread

import scala.collection.mutable
import scala.util.Random
import spout.client.TargetFile
import spout.inject.{RemoteCommand, PacketManager}

class TransmitterThread[T](files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T], packetGen:PacketManager) extends Runnable{
    def run()={
        for(file <- files.values){
            file.server.feedWatchdog()
            file.packetHandler.getPacket().foreach(packet => controller.transmitToPeer(file.targetPeers.toVector(Random.nextInt(file.targetPeers.size)), packet))
        }
    }
}