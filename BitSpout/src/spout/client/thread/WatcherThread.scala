package spout.client.thread

import scala.collection.mutable

import spout.client.TargetFile
import spout.inject.RemoteCommand
import spout.inject.Constants

class WatchingThread[T](files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T]) extends Runnable{   
    def run()={
        for(file <- files.values){
            file.server.feedWatchdog()
            if(file.activePeers.size > Constants.PEER_GOAL){
                val newPeers=file.server.getPeers()
                file.peers++=newPeers
                for(inactivePeer <- file.peers &~ file.activePeers){
                    controller.transmitToPeer(inactivePeer, file.packetHandler.generateStartSignal())  
                }
            }
        }
    }
}