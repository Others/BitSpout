package spout_old.client.thread

import scala.collection.concurrent
import scala.concurrent.Channel

import spout_old.client.TargetFile
import spout_old.inject.HashManager

class DecrypterThread[T](packets:Channel[Seq[Byte]], files:concurrent.Map[(Byte, Seq[Byte]), TargetFile[T]], hashManagers:Map[Byte, HashManager]) extends Runnable{
    def run()={
         while(true){
             val newPacket=packets.read
             val hashType=newPacket(0)
             hashManagers.get(hashType).foreach(hashManager => {
                 val (hash, packet)=hashManager.stripTypeAndHash(newPacket)
                 files.get((hashType, hash)).map(_.packetHandler).foreach { handler => 
                     handler.addPacket(packet)
                     if(handler.canDecrypt()){
                         println("Done with decryption of file:" + hash)
                     }
                 }
             })
         }
    }
}