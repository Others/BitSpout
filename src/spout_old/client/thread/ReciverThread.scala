package spout_old.client.thread

import scala.collection.mutable
import scala.concurrent.Channel

import spout_old.inject.Constants._
import spout_old.client.TargetFile
import spout_old.inject.RemoteCommand
import spout_old.inject.HashManager
import spout_old.inject.PacketManager


class ReciverThread[T](packets:Channel[Seq[Byte]], files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T], packetGen:PacketManager, hashManagers:Map[Byte, HashManager]) extends Runnable{
    def run()={
        while(true){
            val (polled, from)=controller.pollPeers()
            hashManagers.get(polled(1)).foreach{hashManager => 
                val code=polled(0)
                val hashType=polled(1)
                val stripedData=polled.tail
                val (hash, data)=hashManager.stripTypeAndHash(stripedData)
                files.get((hashType, hash)).fold[Unit]({
                    controller.transmitToPeer(from, packetGen.getStopSignal(hashType, hash))
                })(file => {
                    code match{
                        case(SEND_DATA) => {
                            file.targetPeers+=from
                        }
                        case(STOP_DATA) => {
                            file.targetPeers-=from
                        }
                        case(DATA) => {
                            if(file.packetHandler.canDecrypt()){
                                controller.transmitToPeer(from, packetGen.getStopSignal(file.fileHashType, file.fileHash))
                            }else{
                                file.packetHandler.addPacket(stripedData)
                            }
                        }
                        case(_) => 
                    }
                })
            }
        }
    }
}