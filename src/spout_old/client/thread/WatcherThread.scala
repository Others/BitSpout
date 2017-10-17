package spout_old.client.thread

import java.util.TimerTask

import scala.collection.mutable

import spout_old.client.TargetFile
import spout_old.inject.RemoteCommand
import spout_old.inject.Constants
import spout_old.inject.PacketManager

class WatchingThread[T](files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T], packetGen:PacketManager) extends TimerTask{   
    def run()={
        for(file <- files.values){
            file.server.feedWatchdog()
            if(file.activePeers.size > Constants.PEER_GOAL){
                val newPeers=file.server.getPeers()
                file.peers++=newPeers
                for(inactivePeer <- file.peers &~ file.activePeers){
                    controller.transmitToPeer(inactivePeer, packetGen.getStartSignal(file.fileHashType, file.fileHash))  
                }
            }
        }
    }
}