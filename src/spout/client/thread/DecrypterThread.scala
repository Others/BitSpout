package spout.client.thread

import scala.concurrent.Channel

import spout.client.TargetFile
import spout.inject.HashManager

class DecrypterThread[T](packets:Channel[Seq[Byte]], files:Map[(Byte, Seq[Byte]), TargetFile[T]], hashManagers:Map[Byte, HashManager]) extends Runnable{
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